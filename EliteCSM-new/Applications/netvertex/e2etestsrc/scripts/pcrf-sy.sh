#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executePcrfSy() {
    log "########################################################################################################################################\n"
    log "Executing PCRF Sy Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 3600 "./jmeter.sh -n -t PCRF_Sy.jmx -l pcrf_sy_automation_execution.csv -LDEBUG -jpcrf_sy_automation.log"

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for PCRF Sy Call Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/pcrf_sy_automation_execution.csv PCRF_Sy_Automation_Result PCRF_Sy $USERHOMEDIR/logs/automation_report/csv/PCRF_Sy_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi
}

setupPcrfSy() {
    setup PCRF $PCRF_SY_USER
}

