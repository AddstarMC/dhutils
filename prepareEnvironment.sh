#!/bin/sh
if [ -f builder/Spigot.jar]; then
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar --rev 1.12.2
    java -jar BuildTools.jar
    mv Spigot.jar builder/Spigot.jar
else
    mvn install:install-file -DgroupId=org.spigotmc -DartifactId=Spigot -Dversion=1.12.2-R0.1-SNAPSHOT -Dfile=builder/Spigot.jar
fi
