#!/bin/bash
# shellcheck source-path=SCRIPTDIR
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.."
# shellcheck source=../package/heroesofloot2/display.inc
source package/heroesofloot2/display.inc
GAMEDIR="$(mktemp -d)"
trap 'rmdir "$GAMEDIR"' EXIT
for size in 640x480 720x480 720x720 1024x768 1280x720 960x544 320x240; do
    DISPLAY_WIDTH="${size%x*}" DISPLAY_HEIGHT="${size#*x}"
    HEROESOFLOOT2_RESOLUTION=auto
    heroesofloot2_display_setup
    [[ "${display_env[0]}" == "WESTON_HEADLESS_WIDTH=$DISPLAY_WIDTH" ]]
    [[ "${display_env[1]}" == "WESTON_HEADLESS_HEIGHT=$DISPLAY_HEIGHT" ]]
    [[ "${display_java[0]}" == "-Dheroesofloot2.width=$DISPLAY_WIDTH" ]]
    [[ "${display_java[1]}" == "-Dheroesofloot2.height=$DISPLAY_HEIGHT" ]]
done
DISPLAY_WIDTH=0 DISPLAY_HEIGHT=0
heroesofloot2_display_setup
[[ "${#display_env[@]}" == 0 && "${#display_java[@]}" == 0 ]]
for bad in 0x480 640x0 100x100 9999x720 640x480oops '640x480;exit' 640X480; do
    HEROESOFLOOT2_RESOLUTION="$bad"
    if heroesofloot2_display_setup; then echo "Accepted invalid size: $bad"; exit 1; fi
done
HEROESOFLOOT2_RESOLUTION=0720x0480
heroesofloot2_display_setup
[[ "${display_java[0]}" == '-Dheroesofloot2.width=720' ]]
[[ "${display_java[1]}" == '-Dheroesofloot2.height=480' ]]
printf '720x720\r\n' > "$GAMEDIR/resolution.txt"
HEROESOFLOOT2_RESOLUTION=""
heroesofloot2_display_setup
[[ "${display_java[1]}" == '-Dheroesofloot2.height=720' ]]
HEROESOFLOOT2_RESOLUTION=1280x720
heroesofloot2_display_setup
[[ "${display_java[0]}" == '-Dheroesofloot2.width=1280' ]]
rm -- "$GAMEDIR/resolution.txt"
echo 'DISPLAY_CHECKS_OK: sizes, override precedence, CRLF, invalid inputs, automatic fallback'
