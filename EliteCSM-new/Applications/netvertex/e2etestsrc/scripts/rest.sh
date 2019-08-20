#!/usr/bin/env bash

executeRest() {
    log "########################################################################################################################################\n"
    log "Executing Rest Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 1800 "./jmeter.sh -n -t rest_automation.jmx -Jtest.module=rest_automation.csv -Jserverip=$OSIP -Jserverport=$OSPORT -Jtest_run_id=405 -Jchecked_version=26 -Jchecked_revision=11111 -l rest_automation_execution.csv -LDEBUG -jrest_automation.log"
    grep -v ",end sampler," rest_automation_execution.csv | grep -v ",Start PD logs," | grep -v ",Stop PD logs,"  > rest_automation_result.csv

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for Rest Automation :\n"
    log "########################################################################################################################################\n"

    find $TTSBIN -maxdepth 1 -type f -name \*.json -exec mv {} $INPUT_DATA \;
    cp -r $TTSBIN/input_data/* $USERHOMEDIR/logs/automation_report/rest_automation_report/input_data/
    cp -r $TTSBIN/response_data/* $USERHOMEDIR/logs/automation_report/rest_automation_report/response_data/
    java -jar -Dtest.runid=405 -Dtest.check.version=26 -Dtest.check.revision=11111 reportgentool.jar -rest $USERHOMEDIR/logs/automation_report/rest_automation_report $AUTOMATION_REPORT_PATH
    if [ $? = 1 ] ; then
            echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
            UserProcessKill
    fi
}

setupRest() {
    setup PCC $REST_USER
}