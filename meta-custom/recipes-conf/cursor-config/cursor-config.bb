
DESCRIPTION = "Cursor configuration"
MAINTAINER = "geceavsar"
DEBIAN_DEPENDS = ""

SRC_URI = "file://cursor-config.service"

PACKAGE_ARCH = "amd64"

inherit dpkg-raw

do_install() {
    install -d ${D}/etc/systemd/system
    install -m 644 ${WORKDIR}/cursor-config.service ${D}/etc/systemd/system/cursor-config.service
}
