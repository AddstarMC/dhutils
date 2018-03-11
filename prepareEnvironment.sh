#!/bin/sh
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar --rev 1.12.2
java -jar BuildTools.jar
