#!/bin/bash
set -euo pipefail

flag="/var/lib/repart/expand.flag"
stamp="/var/lib/repart/resize.stamp"

[ -e "$flag" ] || exit 0

if ! command -v systemd-repart >/dev/null 2>&1; then
  echo "[repart-grow] systemd-repart not found; skipping expansion."
  rm -f "$flag"
  exit 0
fi

mkdir -p /var/lib/repart

echo "[repart-grow] Growing root and creating /home with systemd-repart..."

systemd-repart --dry-run=no --no-pager --generate-fstab=/etc/fstab

touch "$stamp"
systemctl enable repart-fs-resize.service >/dev/null 2>&1 || true
rm -f "$flag"

echo "[repart-grow] Rebooting to apply the new root partition size..."
systemctl reboot

