#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

   executePcrfDiameter() {
        log "########################################################################################################################################\n"
        log "Executing PCRF Diameter Configuration :\n"
        log "########################################################################################################################################\n"
        ForceTimeOutSec 4800 "./jmeter.sh -n -t PCRF_Diameter_configuration.jmx -l pcrf_diameter_configuration_automation_execution.csv -LDEBUG -jpcrf_diameter_configuration_automation.log"

        restartServer
        cd $TTSBIN

        log "########################################################################################################################################\n"
        log "Executing PCRF Diameter Call Flow :\n"
        log "########################################################################################################################################\n"
        ForceTimeOutSec 4800 "./jmeter.sh -n -t PCRF_Diameter.jmx -l pcrf_diameter_automation_execution.csv -LDEBUG -jpcrf_diameter_automation.log"

        appendCsv pcrf_diameter_automation_execution.csv pcrf_diameter_configuration_automation_execution.csv
        cat pcrf_diameter_configuration_automation_execution.csv > pcrf_diameter_automation_execution.csv

        copyCsvForReporting


        log "########################################################################################################################################\n"
        log "Generating Report for PCRF Diameter Call Flow :\n"
        log "########################################################################################################################################\n"
        moveLogsForReporting
        java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/pcrf_diameter_automation_execution.csv PCRF_Diameter_Automation_Result PCRF_DIA $USERHOMEDIR/logs/automation_report/csv/PCRF_Diameter_Mapping.csv
        if [ $? = 1 ] ; then
                echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
                UserProcessKill
        fi
   }


    setupPcrfDia() {
        setup PCRF $PCRF_DIA_USER
    }
