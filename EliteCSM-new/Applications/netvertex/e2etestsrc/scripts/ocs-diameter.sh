#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executeOcsDiameter() {

    log "########################################################################################################################################\n"
    log "Executing OCS Diameter Configuration :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 3600 "./jmeter.sh -n -t OCS_Diameter_configuration.jmx -l ocs_diameter_configuration_automation_execution.csv -LDEBUG -jocs_diameter_configuration_automation.log"

    restartServer
    cd $TTSBIN


    log "########################################################################################################################################\n"
    log "Executing OCS Diameter Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 3600 "./jmeter.sh -n -t OCS_Diameter.jmx -l ocs_diameter_automation_execution.csv -LDEBUG -jocs_diameter_automation.log"


    appendCsv ocs_diameter_automation_execution.csv ocs_diameter_configuration_automation_execution.csv
    cat ocs_diameter_configuration_automation_execution.csv > ocs_diameter_automation_execution.csv

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for OCS Diameter Call Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/ocs_diameter_automation_execution.csv OCS_Diameter_Automation_Result OCS_DIA $USERHOMEDIR/logs/automation_report/csv/OCS_Diameter_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi
}

setupOcsDiameter(){
    setup OCS $OCS_DIA_USER 
}



