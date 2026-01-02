#!/bin/sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Starting Aeron\n"

java \
  --add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
  -cp ${SCRIPT_DIR}/aeron-all-1.49.0.jar \
  -Daeron.dir=${AERON_DIR} \
  -Daeron.dir.delete.on.start=true \
  -Daeron.dir.delete.on.shutdown=true \
  -Daeron.threading.mode=SHARED \
  io.aeron.driver.MediaDriver
