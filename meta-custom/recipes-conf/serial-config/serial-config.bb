
DESCRIPTION = "Serial port configuration"
MAINTAINER = "geceavsar"

SRC_URI = " file://disable-getty-ttys.path \
            file://disable-getty-ttys.service \
            file://postinst "

inherit dpkg-raw

PACKAGE_ARCH = "amd64"

do_install() {
    install -d ${D}/etc/systemd/system/

    install -m 644 ${WORKDIR}/disable-getty-ttys.path ${D}/etc/systemd/system/disable-getty-ttys.path
    install -m 644 ${WORKDIR}/disable-getty-ttys.service ${D}/etc/systemd/system/disable-getty-ttys.service
}
