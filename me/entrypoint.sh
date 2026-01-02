#!/bin/sh
java \
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
-jar me-1.0-SNAPSHOT-all.jar
