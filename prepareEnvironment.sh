#!/bin/sh
set -ev
if ! [[ -f builder/spigot-${SPIGOT_VERSION}.jar ]]; then
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
    java -jar BuildTools.jar --rev ${SPIGOT_VERSION} > /dev/null
    cp spigot*.jar builder/spigot*.jar
fi