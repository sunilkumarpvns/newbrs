#!/usr/bin/env bash

source ./log-utils.sh
source ./setup.sh
source ./utils.sh

executePcrfRadius() {
    log "########################################################################################################################################\n"
    log "Executing PCRF Radius Configuration :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 4800 "./jmeter.sh -n -t PCRF_Radius_configuration.jmx -l pcrf_radius_configuration_automation_execution.csv -LDEBUG -jpcrf_radius_configuration_automation.log"
    ForceTimeOutSec 4800 "./jmeter.sh -n -t PCRF_Radius_non_cumulative_configuration.jmx -l pcrf_radius_non_cumulative_configuration_automation_execution.csv -LDEBUG -jpcrf_radius_non_cumulative_configuration_automation.log"

    restartServer
    cd $TTSBIN

    log "########################################################################################################################################\n"
    log "Executing PCRF Radius Call Flow :\n"
    log "########################################################################################################################################\n"
    # /bin/sh -c ./jmeter.sh -n -t PCRF_Radius1.jmx -l pcrf_radius_automation_execution1.csv -LDEBUG -jpcrf_radius1_automation.log &
    # ForceTimeOutSec 3600 "./jmeter.sh -n -t PCRF_Radius2.jmx -l pcrf_radius_automation_execution2.csv -LDEBUG -jpcrf_radius2_automation.log"

    /bin/sh -c "./jmeter.sh -n -t PCRF_Radius1.jmx -l pcrf_radius_automation_execution1.csv -LDEBUG -jpcrf_radius1_automation.log" & WPID=$!; sleep 1800 && kill $! & KPID=$!;
    pid1=$WPID
    /bin/sh -c "./jmeter.sh -n -t PCRF_Radius2.jmx -l pcrf_radius_automation_execution2.csv -LDEBUG -jpcrf_radius2_automation.log" & WPID=$!; sleep 1800 && kill $! & KPID=$!;
    pid2=$WPID
    log "########################################################################################################################################\n"
    log "Executing PCRF Radius Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 4800 "./jmeter.sh -n -t PCRF_Radius_non_cumulative.jmx -l pcrf_radius_non_cumulative_automation_execution.csv -LDEBUG -jpcrf_radius_non_cumulative_automation.log"

    while [ ! -z "`ps -ef | grep $pid1 | grep -v grep`" ] || [ ! -z "`ps -ef | grep $pid2 | grep -v grep`" ]
    do
        sleep 20
        echo "Process running for either PCRF_Radius1 or PCRF_Radius2 so waiting for 20 seconds"
    done

    appendCsv pcrf_radius_non_cumulative_configuration_automation_execution.csv pcrf_radius_configuration_automation_execution.csv

    cat pcrf_radius_configuration_automation_execution.csv > pcrf_radius_automation_execution.csv

    appendCsv pcrf_radius_automation_execution1.csv pcrf_radius_automation_execution.csv
    appendCsv pcrf_radius_automation_execution2.csv pcrf_radius_automation_execution.csv
    appendCsv pcrf_radius_non_cumulative_automation_execution.csv pcrf_radius_automation_execution.csv

    copyCsvForReporting


    log "########################################################################################################################################\n"
    log "Generating Report for PCRF Radius Call Flow :\n"
    log "########################################################################################################################################\n"
    moveLogsForReporting
    java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/pcrf_radius_automation_execution.csv PCRF_Radius_Automation_Result PCRF_RAD $USERHOMEDIR/logs/automation_report/csv/PCRF_Radius_Mapping.csv
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi
}

setupPcrfRadius() {
    setup PCRF $PCRF_RAD_USER
}

