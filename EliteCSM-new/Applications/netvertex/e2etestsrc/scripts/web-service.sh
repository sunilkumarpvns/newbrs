#!/usr/bin/env bash

source ./log-utils.sh
source ./utils.sh

executeWebService() {
    log "########################################################################################################################################\n"
    log "Executing PCC Web Service Configuration :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 3600 "./jmeter.sh -n -t PCC_Web_Services_Configuration.jmx -l pcc_wadl_configuration_automation_execution.csv -LDEBUG -jpcc_wadl_configuration_automation.log"

    restartServer
    cd $TTSBIN

    log "########################################################################################################################################\n"
    log "Executing PCC Web Service Verification Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 3600 "./jmeter.sh -n -t PCC_Web_Services.jmx -l wadl_automation_execution.csv -LDEBUG -jwadl_automation.log"

    appendCsv wadl_automation_execution.csv pcc_wadl_configuration_automation_execution.csv
    cat pcc_wadl_configuration_automation_execution.csv > wadl_automation_execution.csv


    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for Web Service Verification Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/wadl_automation_execution.csv WADL_Automation_Result WADL $USERHOMEDIR/logs/automation_report/csv/PCC_WADL_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR : web serviece" >> $AUTOMATION_REPORT_PATH/error.log
    fi
}
