#!/bin/sh
set -ev
if ! [[ -f builder/Spigot-${SPIGOT_VERSION}.jar ]]; then
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
    java -jar BuildTools.jar --rev ${SPIGOT_VERSION}
    cp Spigot*.jar builder/Spigot*.jar
fi