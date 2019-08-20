#!/usr/bin/env bash

source ./log-utils.sh
source ./utils.sh

setup(){
    cd $CURRENTPATH
    SUB_MODULE=$2
    MODULE=$1
    mkdir ../../multiple_node_setup_data/$SUB_MODULE
    cp -r ../* ../../multiple_node_setup_data/$SUB_MODULE
    cd ../../multiple_node_setup_data/$SUB_MODULE/scripts
    set_up_variables_for_installation $SUB_MODULE ${WORKSPACE}/installation/$SUB_MODULE $MODULE
    log "Installing Application Setup"
    sleep 20
    bash -x install-apps.sh >> $AUTOMATION_EXECUTION_LOGS/$SUB_MODULE$INSTALLATION_LOG_SUFIX 2>&1 &

    rm -rf ../../multiple_node_setup_data/$SUB_MODULE/softwares
    cd $CURRENTPATH
}
