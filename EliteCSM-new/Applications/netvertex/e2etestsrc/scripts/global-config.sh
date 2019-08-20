#!/usr/bin/env bash

executeGlobalConfig() {
    log "########################################################################################################################################\n"
    log "Global Configuration Provisioning :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 540 "./jmeter.sh -n -t PCC_Global_Configurations.jmx -l global_configuration_execution.csv -LDEBUG -jglobal_configuration_automation.log"

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for Global Configuration :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    local filePrefix=`echo $USR | awk -F "_NETVERTEX" '{print $1}'`
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $filePrefix $USERHOMEDIR/logs/automation_report/csv/global_configuration_execution.csv Global_Configurations GLOBAL_CONF
    if [ $? = 1 ] ; then
        echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
        UserProcessKill
        exit 1
    fi
}
