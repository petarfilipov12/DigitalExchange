#!/bin/sh

java \
	--add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
	-jar app.jar
