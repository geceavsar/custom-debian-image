
DESCRIPTION = "Sound configuration"
MAINTAINER = "geceavsar"

DEBIAN_DEPENDS = "pulseaudio, alsa-utils"

SRC_URI = " file://audio.conf \
            file://analog-output-custom.conf \
            file://hdmi-output.conf "

PACKAGE_ARCH = "amd64"

inherit dpkg-raw 

do_install() {
    install -d ${D}/usr/share/pulseaudio/alsa-mixer/profile-sets/
    install -d ${D}/usr/share/pulseaudio/alsa-mixer/paths/
    install -d ${D}/etc/pulse/

    install -m 644 ${WORKDIR}/audio.conf ${D}/usr/share/pulseaudio/alsa-mixer/profile-sets/audio.conf
    install -m 644 ${WORKDIR}/analog-output-custom.conf ${D}/usr/share/pulseaudio/alsa-mixer/paths/analog-output-custom.conf
    install -m 644 ${WORKDIR}/hdmi-output.conf ${D}/usr/share/pulseaudio/alsa-mixer/paths/hdmi-output.conf
}
