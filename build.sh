#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
MACHINE="x86-pc"
DEPLOY_DIR="$SCRIPT_DIR/deploy"
CLEAN_BUILD="false"
BUILD_INSTALLER="false"
START_TIME=$(date +%s)

print_usage() {
    cat <<'EOF'
usage: build.sh [OPTIONS]

Options:
  --qemu                  Create a QEMU image for debugging (default:x86-pc)
  --clean-build           Clean and reset previous build artifacts and re-clone ISAR (default: false)
  --deploy <dir>          Directory (inside script directory) for collected artifacts (default: deploy)
  --help                  Show this help text and exit
EOF

}


if ! options=$(getopt -o '' --long deploy:,clean-build,qemu,help -- "$@"); then
    print_usage
    exit 1
fi
eval set -- "$options"


while :; do
    case "$1" in
        --qemu)
            MACHINE="qemuamd64"
            ;;
        --deploy)
            shift
            DEPLOY_DIR=$1
            ;;
        --clean-build)
            CLEAN_BUILD="true"
            ;;
        --help)
            shift
            print_usage
            exit 0
            ;;
        --)
            shift
            break
            ;;
    esac

    shift
done

if [ "$CLEAN_BUILD" == "true" ]; then
    echo "Cleaning previous build artifacts..."
    rm -rf "$DEPLOY_DIR"
    rm -rf $SCRIPT_DIR/build
    rm -rf $SCRIPT_DIR/isar
fi

    
export KAS_MACHINE="$MACHINE"
export KAS_TARGET="custom-image"
export DEBIAN_FRONTEND="noninteractive"
export DEBCONF_NONINTERACTIVE_SEEN="true"

echo "Building the main OS image for $MACHINE"

./kas-container --runtime-args "-e HOSTNAME=custom-debian" build kas-common.yaml:kas-image.yaml

if [ $? -ne 0 ]; then
    echo "Image build failed. Exiting."
    exit 1
fi

echo "Collecting build artifacts..."
mkdir -p "$DEPLOY_DIR"
cp $SCRIPT_DIR/build/tmp/deploy/images/$MACHINE/*.wic "$DEPLOY_DIR/"

END_TIME=$(date +%s)
ELAPSED_TIME=$((END_TIME - START_TIME))

echo "Total build time: $ELAPSED_TIME seconds"
echo "Build and artifact collection complete. Artifacts are located in $DEPLOY_DIR"
