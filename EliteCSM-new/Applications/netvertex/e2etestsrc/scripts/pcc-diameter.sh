#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executePccDiameter() {
    log "########################################################################################################################################\n"
    log "Executing PCC Diameter Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 2400 "./jmeter.sh -n -t PCC_Diameter_configuration.jmx -l pcc_diameter_configuration_automation_execution.csv -LDEBUG -jpcc_diameter_configuration_automation.log"

    restartServer
    cd $TTSBIN


    log "########################################################################################################################################\n"
    log "Executing PCC Diameter Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 2400 "./jmeter.sh -n -t PCC_Diameter.jmx -l pcc_diameter_automation_execution.csv -LDEBUG -jpcc_diameter_automation.log"

    appendCsv pcc_diameter_automation_execution.csv pcc_diameter_configuration_automation_execution.csv
    cat pcc_diameter_configuration_automation_execution.csv > pcc_diameter_automation_execution.csv

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for PCC Diameter Call Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/pcc_diameter_automation_execution.csv PCC_Diameter_Automation_Result PCC_DIA $USERHOMEDIR/logs/automation_report/csv/PCC_Diameter_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi

}



setupPccDiameter() {
    setup PCC $PCC_DIA_USER
}

