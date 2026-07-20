DESCRIPTION = "SSH config"
MAINTAINER = "geceavsar"

inherit dpkg-raw

SRC_URI = "file://postinst \
           file://authorized_keys "
# add your keys to /home/user/.ssh/authorized_keys or /etc/.ssh/authorized_keys file 

DEBIAN_DEPENDS += "openssh-server"
DEPENDS += "sshd-regen-keys"

PACKAGE_ARCH = "amd64"

do_install() {
    install -d ${D}/home/user/.ssh/
    install -m 0600 ${WORKDIR}/authorized_keys ${D}/home/user/.ssh/authorized_keys
}
