# Custom Debian image
#
# This image extends the base isar-image-base with specific configurations

DESCRIPTION = "Custom Debian image"

require recipes-core/images/isar-image-base.bb

inherit debian-repos

PV = "1.0"

export BB_ENV_PASSTHROUGH_ADDITIONS="$BB_ENV_PASSTHROUGH_ADDITIONS HOSTNAME"

# IMAGE_PREINSTALL variable is for the system packages that will be pulled from 
# Debian upstreams. Beware that base image lacks many of the packages
# existing in Debian distributions by default.

IMAGE_PREINSTALL += " \
    sudo \
    iproute2 \
    ifupdown \
    build-essential \
    systemd \
    pkg-config \
    cmake \
    git \
    vim \
    nano \
    curl \
    locales \
    unzip \
    openbox \
    lightdm \
    lightdm-gtk-greeter \
    libpam-systemd \
    alsa-utils \
    openssh-server \
    bash \
    bash-completion \
    plymouth \
    pavucontrol \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    firmware-linux \
    intel-microcode \
    xorg \
    x11-common \
    xserver-xorg-video-all \
    xserver-xorg-input-all \
    desktop-base \
    xdg-utils \
    xinput \
    libsystemd0 \
    util-linux-extra \
    libsystemd-shared \
    accountsservice \
    network-manager \
    polkitd \
    pkexec \
    silversearcher-ag \
    logrotate \
    pulseaudio \
    systemd-timesyncd \
    fail2ban \
    openssl \
    hunspell \
    autoconf \
    libtool \
    xvfb \
    wmctrl \
    e2fsprogs \
    dosfstools \
    unclutter \
    unclutter-xfixes \
    ca-certificates \
"

TEMPLATE_VARS = "HOSTNAME"
CUSTOMIZATIONS += "hostname"

IMAGE_INSTALL += " \
    ssh-server-config \
    sshd-regen-keys \
    display-config \
    cursor-config \
    sound-config \
"

do_image_install[depends] += "base-passwd:do_populate_sysroot"

EXTRA_IMAGECMD:ext4 = "-N 262144"
ISAR_RELEASE_CMD = "git rev-parse HEAD"

# Add user configurations
# Passwords were encrypted using following command:
#   openssl passwd -6 <pwd>
# using SHA256/512 scheme

# Yes, you even need to generate the root user from scratch.

GROUPS += "root"
GROUP_root[gid] = "0"
USERS += "root"

USER_root[flags] = "system create-home"
USER_root[password] = "$6$JfeyGMYrjU88Hp9c$Z0niNh5TiR/l0Uq6UCoOZLV5EnmudxuyaYbsilgZH/kNawAzdFt2aAQjbMPRMoZD1OtU2mPb6u19A1JYg2Ru40"

# Use these flags for entering clear-text passwords
# USER_root[flags] = "system create-home clear-text-password"
# USER_root[password] = "root"

USER_root[uid] = "0"
USER_root[gid] = "root"
USER_root[shell] = "/bin/bash"
USER_root[home] = "/root"

########################

USERS += "user"
GROUPS += "user"
# first non-root user almost always has its uid 1000
GROUP_user[gid] = "1000" 
USER_user[home] = "/home/user"

USER_user[flags] = "create-home"
USER_user[password] = "$6$XGN/08Xv2c5edsWy$.FzbRpmbbqjkoyV/Wj9Kuj8kQzRYxywgUuEyRWWKYBXAD5vMiBorPomuVocMWZp.U45NuN2r6L5cZ5uX0Oi/w1"

USER_user[uid] = "1000"
USER_user[gid] = "user"
USER_user[groups] = "adm dialout sudo audio video plugdev netdev"
USER_user[shell] = "/bin/bash"
