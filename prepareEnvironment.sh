#!/bin/sh
set -ev
if [ -f builder/Spigot.jar ]; then
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
    java -jar BuildTools.jar --rev ${Spigot.version}
    cp Spigot.jar builder/Spigot.jar
fi