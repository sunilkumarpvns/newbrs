#!/usr/bin/env bash

cd ../../..

./gradlew clean netvertex:installDist

cd Applications/netvertex

docker build -t netvertex:7.2.4.1 -f docker/Dockerfile .