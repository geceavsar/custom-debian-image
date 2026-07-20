DESCRIPTION = "Silent Boot Configuration"
MAINTAINER = "geceavsar"

SRC_URI = " \
    file://grub-default \
    file://10-silent-boot.conf \
    file://silent-boot-apply.sh \
    file://silent-boot-apply.service \
    file://postinst "

DEBIAN_DEPENDS = "grub-pc, grub-pc-bin"

PACKAGE_ARCH = "amd64"

inherit dpkg-raw

do_install() {
    install -d ${D}/etc/default/grub.d/
    install -m 0644 ${WORKDIR}/grub-default ${D}/etc/default/grub.d/silent-boot.cfg

    # Persistent quiet console log level.
    install -d ${D}/etc/sysctl.d
    install -m 0644 ${WORKDIR}/10-silent-boot.conf ${D}/etc/sysctl.d/10-silent-boot.conf

    # First-boot GRUB regeneration (update-grub cannot run during the build).
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/silent-boot-apply.sh ${D}/usr/sbin/silent-boot-apply

    install -d ${D}/etc/systemd/system
    install -m 0644 ${WORKDIR}/silent-boot-apply.service ${D}/etc/systemd/system/silent-boot-apply.service
    install -d ${D}/etc/systemd/system/multi-user.target.wants
    ln -sf ../silent-boot-apply.service \
        ${D}/etc/systemd/system/multi-user.target.wants/silent-boot-apply.service

    # One-shot trigger; silent-boot-apply deletes it after the first run.
    install -d ${D}/var/lib/silent-boot
    install -m 0644 /dev/null ${D}/var/lib/silent-boot/apply.flag
}
