# custom-image

Build custom Debian images with BitBake, ISAR, and kas. For personal use.

> **Note:** This repository is for **personal use**. It is not a supported
> product and comes with no warranty.

## Overview

This project builds reproducible Debian images. Three tools take you from a clean
checkout to a bootable `.wic` image with one command.

### What are kas containers and ISAR, and why use them?

- **BitBake** runs the build. It reads recipes (`.bb` files) and layers, works
  out the task order, and builds the image. It comes from Yocto/OpenEmbedded.

- **ISAR** (*Integration System for Automated Root filesystem generation*) is a
  set of BitBake layers that build **Debian** root filesystems from **pre-built
  Debian packages** instead of compiling everything from source. This makes
  builds much faster than a Yocto build and still lets you use recipes, layers,
  and custom config. Use it when you want a custom Debian image, built the same
  way every time, without maintaining a distribution from scratch.

- **kas** sets up and drives the build. A kas YAML file pins the layers, the
  ISAR revision, the target machine, and the local config, so anyone can repeat
  the same build. The **kas container** packs kas and every build dependency
  (BitBake, ISAR tools, host packages) into a Docker image, so you need not
  install the toolchain on your host. Use it to keep builds repeatable and your
  host clean.

In short: **kas** pins the environment, **ISAR** supplies the Debian build
layers, and **BitBake** runs the recipes to build the image.

## Requirements

You need:

- **Docker** -> runs the kas container that holds the build toolchain.
- **QEMU** -> needed only to boot or debug the image in a virtual machine (that
  is, when you build with `--qemu`).

You need not install BitBake, ISAR, or kas on the host. The kas container
supplies the rest.

## Supported machines

The default target is `x86-pc`. ISAR ships more machines under
`isar/kas/machine/`. Pick one with `KAS_MACHINE`:

| Machine | Description |
| --- | --- |
| `x86-pc` | 64-bit x86 PC or server (the default). |
| `qemuamd64` | 64-bit x86 image for QEMU. |
| `qemuamd64-cip` | QEMU amd64 on the CIP (Civil Infrastructure Platform) kernel. |
| `qemuamd64-iso` | QEMU amd64 that builds a bootable ISO. |
| `qemuamd64-sb` | QEMU amd64 with UEFI Secure Boot. |
| `qemui386` | 32-bit x86 image for QEMU. |
| `qemuarm` | 32-bit ARM (ARMv7) image for QEMU. |
| `qemuarm64` | 64-bit ARM (AArch64) image for QEMU. |
| `qemumipsel` | Little-endian MIPS image for QEMU. |
| `qemuriscv64` | 64-bit RISC-V image for QEMU. |
| `container-amd64` | Small amd64 root filesystem for containers. |
| `virtualbox` | Image for Oracle VirtualBox. |
| `vmware` | Image for VMware. |
| `rpi-arm` | Raspberry Pi, older ARMv6 boards. |
| `rpi-arm-v7` | Raspberry Pi, 32-bit ARMv7 boards. |
| `rpi-arm-v7l` | Raspberry Pi, 32-bit ARMv7 boards, hard-float (armhf). |
| `rpi-arm64-v8` | Raspberry Pi, 64-bit ARMv8 (AArch64) boards. |
| `bananapi` | Banana Pi single-board computer. |
| `beagleplay` | BeagleBoard BeaglePlay board. |
| `de0-nano-soc` | Terasic DE0-Nano-SoC (Cyclone V) FPGA board. |
| `hikey` | HiKey (HiSilicon Kirin) 96Boards board. |
| `imx6-sabrelite` | NXP i.MX6 SABRE Lite board. |
| `nanopi-neo` | FriendlyELEC NanoPi NEO board. |
| `nanopi-neo-efi` | FriendlyELEC NanoPi NEO, EFI boot. |
| `phyboard-mira` | PHYTEC phyBOARD-Mira (i.MX6) board. |
| `sifive-fu540` | SiFive FU540 (HiFive Unleashed) RISC-V board. |
| `starfive-visionfive2` | StarFive VisionFive 2 RISC-V board. |
| `stm32mp15x` | STMicroelectronics STM32MP15x MPU boards. |

## Building

Make sure Docker runs, then use `build.sh`.

Build the default `x86-pc` image:

```bash
./build.sh
```

Build a QEMU image for debugging:

```bash
./build.sh --qemu
```

Clean old artifacts, re-clone ISAR, and rebuild from scratch:

```bash
./build.sh --clean-build
```

Put artifacts in a chosen directory (relative to the repository root):

```bash
./build.sh --deploy my-artifacts
```

Show all options:

```bash
./build.sh --help
```

On success, `build.sh` copies the `.wic` image into the deploy directory
(`deploy/` by default).

## Bundled recipes (BitBakes)

The `meta-custom` layer ships ready-made recipes. Each one **solves a common
problem** you hit while building a usable Debian image, and they are here **for
reference**: pick the ones you need, change them, or use them as templates. Each
config recipe uses ISAR's `dpkg-raw` class to place files (systemd units, config
snippets, scripts) into the image as a Debian package.

| Recipe | Problem it solves |
| --- | --- |
| `recipes-core/images/custom-image.bb` | The main image recipe. It extends `isar-image-base`, installs the Debian packages the image needs, pulls in the config recipes below, sets the hostname, and defines the `root` and `user` accounts (with SHA-512 hashed passwords). Start here to see how the parts fit. |
| `recipes-conf/display-config` | A small graphical session (Openbox + LightDM + GTK greeter) instead of a full desktop, for a light kiosk-style UI. |
| `recipes-conf/cursor-config` | A systemd service that controls cursor behaviour in the graphical session. |
| `recipes-conf/sound-config` | PulseAudio/ALSA profile and path config so analog and HDMI audio work. |
| `recipes-conf/ssh-server-config` | Sets up the OpenSSH server and pre-loads `authorized_keys`; depends on key regeneration so each image gets its own host keys. |
| `recipes-conf/serial-config` | Controls the serial console getty (turns getty off on chosen TTYs) with systemd path/service units. |
| `recipes-conf/silent-boot-config` | A GRUB drop-in for a quiet, splash boot with less console output. |
| `recipes-conf/systemd-repart-config` | Declares the on-disk layout (`repart.d` drop-ins for ESP, root, home) so partitions grow on first boot. |
| `recipes-conf/repart-grow` | Two-stage first-boot growth: runs `systemd-repart`, reboots, then resizes the ext4 filesystems to fill the disk. Useful when you flash a fixed-size `.wic` onto a larger disk. |

> These recipes are opinionated and target `amd64`. Treat them as a starting
> point: adjust the files, dependencies, and target machine for your hardware.

## Partition layout options

Different jobs need different layouts. By default this repository grows the disk
on first boot and gives 50% of the space to `/` and 50% to `/home`. Other
choices:

- **ISAR defaults** -> Leave `WKS_FILE` unset in `kas-image.yaml`, and remove the
  `systemd-repart-config` and `repart-grow` recipes from `custom-image.bb`.

- **Fixed-size partitions** -> Remove the `systemd-repart-config` and
  `repart-grow` recipes from `custom-image.bb`, then set your partitions in
  `custom-partitions*.wks`. wic accepts only a limited set of `part` arguments;
  see the [WIC manual](https://docs.yoctoproject.org/dev-manual/wic.html) and the
  [wks reference](https://docs.yoctoproject.org/ref-manual/kickstart.html).

    You can also use ISAR's `expand-on-first-boot` recipe: it grows the last
    partition in your `custom-partitions*.wks` to the end of the disk.

    Fixed-size partitions make the image larger.

- **Expandable partitions** -> Edit the `systemd-repart-config` and `repart-grow`
  recipes to suit your needs.

## Debugging

Besides flashing the image onto real hardware, you can inspect it three ways: run
it in a virtual machine, open the built filesystem, or read the build tree.

### 1. Boot the image in QEMU

Build a QEMU image and boot it in a scratch VM. This is the fastest way to check
boot behaviour, first-boot logic (partition growth, service startup), and the
graphical session without real hardware.

- Build the QEMU image (targets the `qemuamd64` machine):
```bash
./build.sh --qemu
```

- Copy it and make it bigger so first-boot growth has room:

```bash
cp build/tmp/deploy/images/qemuamd64/*.wic disk.wic
qemu-img resize -f raw disk.wic 20G
```

- Make a writable copy of the UEFI variables (the image boots through GRUB/EFI):
```bash
cp /usr/share/OVMF/OVMF_VARS_4M.fd OVMF_VARS.fd

qemu-system-x86_64 \
  -machine q35,accel=kvm -cpu host -smp 4 -m 4096 \
  -drive if=pflash,format=raw,unit=0,readonly=on,file=/usr/share/OVMF/OVMF_CODE_4M.fd \
  -drive file=disk.wic,format=raw,if=virtio \
  -netdev user,id=net0,hostfwd=tcp::2222-:22 -device virtio-net-pci,netdev=net0 \
  -vga virtio -boot menu=on
```

- `-netdev ... hostfwd=tcp::2222-:22` maps the guest's SSH port to the host, so
  you can reach the VM with `ssh -p 2222 user@localhost` (the `ssh-server-config`
  recipe sets up the keys and user).
- With no KVM on the host, drop `accel=kvm` and use `-cpu max` in place of
  `-cpu host`.
- To retest first boot from scratch, copy the `.wic` over `disk.wic` again and
  delete `OVMF_VARS.fd`.

### 2. Open the built root filesystem

You often need not boot at all. You can read or change the image files directly.

- **Unpacked rootfs:** the built root filesystem sits under
  `build/tmp/deploy/schroot-target/debian-*`. Browse it directly, or `chroot`
  into it to run commands as if inside the target. First bind-mount the kernel
  virtual filesystems from your host:

  ```bash
  sudo mount --bind /dev  build/tmp/deploy/schroot-target/debian-*/dev
  sudo mount --bind /proc build/tmp/deploy/schroot-target/debian-*/proc
  sudo mount --bind /sys  build/tmp/deploy/schroot-target/debian-*/sys
  sudo chroot build/tmp/deploy/schroot-target/debian-* /bin/bash
  # ... inspect packages, files, configs ...
  # Unmount /dev, /proc, /sys again when done.
  ```

- **Mount the `.wic` image:** attach it to a loop device with partition scanning,
  then mount the partition you want (partition 2 is `/` in this layout):

  ```bash
  sudo losetup --find --partscan --show build/tmp/deploy/images/qemuamd64/*.wic  # e.g. /dev/loop0
  sudo mount /dev/loop0p2 /mnt        # root partition
  sudo mount /dev/loop0p1 /mnt/boot/efi   # ESP, if needed
  # ... inspect ...
  sudo umount -R /mnt
  sudo losetup -d /dev/loop0
  ```

### 3. Read the build tree

When a recipe fails or a package misbehaves, the build tree keeps the full
record.

- **Per-recipe work directory:** each recipe builds under
  `build/tmp/work/debian-<distro>-<arch>/<recipe>/...`. Inside you will find:
  - `temp/log.do_<task>` -> the log of each task (`do_fetch`, `do_build`,
    `do_install`, `do_deploy`, ...), plus `run.do_<task>` (the script that ran).
  - the unpacked sources, the staged files, and the built `.deb` for `dpkg-raw`
    recipes.
- **Image and apt logs:** the root filesystem build, `apt` installs, and
  bootstrap steps log under the image recipe's work directory
  (`build/tmp/work/.../custom-image/...`) and its `temp/` logs.
- **Re-run one recipe** without rebuilding everything, from the kas container
  shell:

  ```bash
  KAS_MACHINE=qemuamd64 ./kas-container shell kas-common.yaml:kas-image.yaml \
    -c "bitbake -c <task> <recipe>"        # e.g. -c do_install silent-boot-config
  ```

  Use `-c cleansstate <recipe>` to drop its cached output and force a clean
  rebuild, or `-c devshell <recipe>` for a shell with the recipe's build
  environment set up.

## License

See [LICENSE](LICENSE). This repository, ISAR, and kas use the MIT license. ISAR
metadata mixes MIT and GPLv2, and each Debian package in an image keeps its own
upstream license.
