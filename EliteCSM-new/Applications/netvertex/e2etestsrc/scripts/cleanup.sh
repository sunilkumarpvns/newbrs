#!/usr/bin/env bash

source ./log-utils.sh
source ./utils.sh
source ./server-utils.sh
source ./voltdb.sh

if [ -z "$WORKSPACE" ]
then
      log "\$WORKSPACE is empty"
      exit 0;
else
      log "\$WORKSPACE is $WORKSPACE"
fi

stopTail
killServers
killVoltDB

