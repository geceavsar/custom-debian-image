#!/bin/bash
set -euo pipefail

stamp="/var/lib/repart/resize.stamp"
[ -e "$stamp" ] || exit 0

resize_ext4() {
  mnt="$1"
  dev="$(findmnt -n -o SOURCE --target "$mnt" 2>/dev/null || true)"
  [ -b "$dev" ] || return 0
  fstype="$(findmnt -n -o FSTYPE --target "$mnt" 2>/dev/null || true)"
  if [ "$fstype" = "ext4" ]; then
    echo "[repart-resize] Resizing $mnt ($dev)"
    resize2fs "$dev" || true
  fi
}

resize_ext4 /
resize_ext4 /home

rm -f "$stamp"
systemctl disable repart-fs-resize.service >/dev/null 2>&1 || true
exit 0

