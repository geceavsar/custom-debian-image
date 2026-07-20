SUMMARY = "Systemd-repart partition layout"
DESCRIPTION = "allows growth on first boot."
MAINTAINER = "geceavsar"

SRC_URI = "file://10-esp.conf \
            file://20-root.conf \
            file://30-home.conf "

inherit dpkg-raw 

do_install() {
    install -d ${D}/usr/lib/repart.d
    install -m 0644 ${WORKDIR}/10-esp.conf  ${D}/usr/lib/repart.d/10-esp.conf
    install -m 0644 ${WORKDIR}/20-root.conf ${D}/usr/lib/repart.d/20-root.conf
    install -m 0644 ${WORKDIR}/30-home.conf ${D}/usr/lib/repart.d/30-home.conf
}
