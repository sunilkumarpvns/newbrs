#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executeOcsRadius() {
    log "########################################################################################################################################\n"
    log "Executing OCS Radius Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 3600 "./jmeter.sh -n -t OCS_Radius.jmx -l ocs_radius_automation_execution.csv -LDEBUG -jocs_radius_automation.log"

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for OCS Radius Call Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/ocs_radius_automation_execution.csv OCS_Radius_Automation_Result OCS_RAD $USERHOMEDIR/logs/automation_report/csv/OCS_Radius_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi
}

setupOcsRadius() {

    setup OCS $OCS_RAD_USER
}

