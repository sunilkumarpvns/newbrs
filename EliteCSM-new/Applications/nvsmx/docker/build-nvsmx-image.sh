#!/usr/bin/env bash

cd ../../..

./gradlew clean nvsmx:explodedWar

cd Applications/nvsmx

docker build -t netvertexsm:7.2.4.1 -f docker/Dockerfile .
