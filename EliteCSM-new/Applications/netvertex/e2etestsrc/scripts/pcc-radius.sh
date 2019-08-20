#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executePccRadius() {
    log "########################################################################################################################################\n"
    log "Executing PCC Radius Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 1080 "./jmeter.sh -n -t PCC_Radius.jmx -l pcc_radius_automation_execution.csv -LDEBUG -jpcc_radius_automation.log"

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for PCC Radius Call Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/pcc_radius_automation_execution.csv PCC_Radius_Automation_Result PCC_RAD $USERHOMEDIR/logs/automation_report/csv/PCC_Radius_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi
}

setupPccRadius() {
    setup PCC $PCC_RAD_USER
}

