DESCRIPTION = "Minimal display configuration"
MAINTAINER = "geceavsar"

DEBIAN_DEPENDS = "openbox, lightdm, lightdm-gtk-greeter, x11-xserver-utils, xdotool, inotify-tools, wmctrl"

SRC_URI = "file://minimal-window-manager \
           file://minimal-session \
           file://minimal.desktop \
           file://99-minimal-session.conf \
           file://rc.xml "

PACKAGE_ARCH = "amd64"

inherit dpkg-raw 

do_install() {
    install -d ${D}/usr/bin/
    install -d ${D}/etc/lightdm/lightdm.conf.d
    install -d ${D}/usr/share/xsessions/
    install -d ${D}/home/user/.config/openbox
    install -d ${D}/etc/X11
    install -d ${D}/etc/systemd/system/graphical.target.wants
    
    install -m 755 ${WORKDIR}/minimal-session ${D}/usr/bin/minimal-session
    install -m 644 ${WORKDIR}/99-minimal-session.conf ${D}/etc/lightdm/lightdm.conf.d/99-minimal.conf
    install -m 644 ${WORKDIR}/minimal.desktop ${D}/usr/share/xsessions/minimal.desktop
    install -m 644 ${WORKDIR}/rc.xml ${D}/home/user/.config/openbox/rc.xml
    
    # Set lightdm as default display manager
    echo "/usr/sbin/lightdm" > ${D}/etc/X11/default-display-manager
    
    # Ensure display-manager.service is enabled
    ln -sf /etc/systemd/system/display-manager.service ${D}/etc/systemd/system/graphical.target.wants/display-manager.service
}
