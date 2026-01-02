#!/bin/sh
java \
  --add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
  -cp aeron-all-1.48.6.jar \
  -Daeron.print.configuration=true \
  io.aeron.driver.MediaDriver
