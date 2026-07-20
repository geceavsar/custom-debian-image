DESCRIPTION = "First-boot disk expansion via systemd-repart: grow root, create /home, reboot, then resize ext4."
MAINTAINER = "geceavsar"
DEBIAN_DEPENDS = "e2fsprogs, systemd-repart"
PACKAGE_ARCH = "amd64"

SRC_URI = " file://repart-grow-firstboot.sh \
            file://repart-fs-resize.sh \
            file://repart-grow-firstboot.service \
            file://repart-fs-resize.service "

inherit dpkg-raw 

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/repart-grow-firstboot.sh ${D}/usr/sbin/repart-grow-firstboot
    install -m 0755 ${WORKDIR}/repart-fs-resize.sh ${D}/usr/sbin/repart-fs-resize

    install -d ${D}/etc/systemd/system
    install -m 0644 ${WORKDIR}/repart-grow-firstboot.service ${D}/etc/systemd/system/repart-grow-firstboot.service
    install -m 0644 ${WORKDIR}/repart-fs-resize.service ${D}/etc/systemd/system/repart-fs-resize.service

    # enable the first-boot expansion service. the resize service is enabled at
    # runtime by the first-boot script, just before it triggers the reboot, so
    # it only ever runs on the boot after repartitioning.
    install -d ${D}/etc/systemd/system/multi-user.target.wants
    ln -sf ../repart-grow-firstboot.service \
        ${D}/etc/systemd/system/multi-user.target.wants/repart-grow-firstboot.service

    ln -sf /dev/null ${D}/etc/systemd/system/systemd-repart.service

    install -d ${D}/var/lib/repart
    install -m 0644 /dev/null ${D}/var/lib/repart/expand.flag
}
