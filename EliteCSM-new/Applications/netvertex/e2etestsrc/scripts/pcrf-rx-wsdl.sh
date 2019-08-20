#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executeRxWsdl() {
        log "########################################################################################################################################\n"
        log "Executing PCRF Rx And WSDL Diameter Configuration :\n"
        log "########################################################################################################################################\n"
        ForceTimeOutSec 4800 "./jmeter.sh -n -t PCRF_Diameter_configuration.jmx -l rx_wsdl_configuration_automation_execution.csv -LDEBUG -jrx_wsdl_configuration_automation.log"

        restartServer
        cd $TTSBIN

        log "########################################################################################################################################\n"
        log "Executing PCRF Rx Diameter Call Flow :\n"
        log "########################################################################################################################################\n"
        ForceTimeOutSec 4800 "./jmeter.sh -n -t PCRF_Rx_wsdl_Diameter.jmx -l rx_wsdl_automation_execution.csv -LDEBUG -jrx_wsdl_configuration_automation.log"

        appendCsv rx_wsdl_automation_execution.csv rx_wsdl_configuration_automation_execution.csv
        cat rx_wsdl_configuration_automation_execution.csv > rx_wsdl_automation_execution.csv
        copyCsvForReporting



        log "########################################################################################################################################\n"
        log "Generating Report for PCRF Diameter Call Flow :\n"
        log "########################################################################################################################################\n"
        moveLogsForReporting
        java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/rx_wsdl_automation_execution.csv Rx_WSDL_Automation_Result RX_WSDL $USERHOMEDIR/logs/automation_report/csv/PCRF_Rx_Wsdl_Diameter_Mapping.csv
        if [ $? = 1 ] ; then
                echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
                UserProcessKill
        fi

}

setupRxWsdl(){
    setup PCRF $RX_WSDL_DIA_USER
}

