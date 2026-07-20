#!/bin/sh
set -e

# Regenerate grub.cfg so the /etc/default/grub.d/silent-boot.cfg drop-in
# (quiet splash, hidden menu, timeout 0, ...) actually takes effect.
#
# This runs at RUN TIME rather than in the recipe postinst because during the
# offline ISAR image build there is no root block device, so grub-probe fails
# with: "cannot find a device for / (is /dev mounted?)".

flag=/var/lib/silent-boot/apply.flag
[ -e "$flag" ] || exit 0

if [ -x /usr/sbin/update-grub ]; then
    echo "[silent-boot] Applying silent GRUB settings..."
    update-grub || true
fi

rm -f "$flag"
systemctl disable silent-boot-apply.service >/dev/null 2>&1 || true
exit 0
