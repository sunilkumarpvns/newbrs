#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executeOcsRo() {
    log "########################################################################################################################################\n"
    log "Executing OCS Ro Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 3600 "./jmeter.sh -n -t OCS_Ro.jmx -l ocs_ro_automation_execution.csv -LDEBUG -jocs_ro_automation.log"

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for OCS Ro Call Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/ocs_ro_automation_execution.csv OCS_Ro_Automation_Result OCS_Ro $USERHOMEDIR/logs/automation_report/csv/OCS_Ro_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi
}

setupOcsRo() {
    setup OCS $OCS_RO_USER
}

