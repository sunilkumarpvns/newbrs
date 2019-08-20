#!/usr/bin/env bash

source ./log-utils.sh
source ./utils.sh


copylogs() {
    stopTcpDump
    cp -r $NV_HOME/logs/* $LOGS_AND_PCAP_DIR
    cp -r $PD_HOME/../logs/* $LOGS_AND_PCAP_DIR
    #to reduce the size of log file
    grep -v "org.hibernate" $PD_HOME/logs/catalina.out > $PD_HOME/logs/catalina.temp.log
    mv -f $PD_HOME/logs/catalina.temp.log $PD_HOME/logs/catalina.out
    cp -r $PD_HOME/logs/* $LOGS_AND_PCAP_DIR
}

startTcpDump() {
    /usr/sbin/tcpdump -s 0 -i any port ${diameterport} or ${nvrestport} or ${pdport} or ${nvadminport} or ${radiusport} or ${jmxremoteport} or ${pdshutdownport} -w ${LOGS_AND_PCAP_DIR}/automation.pcap  &
    TCP_DUMP_ID=$!
    echo $TCP_DUMP_ID
}

stopTcpDump() {
    kill $TCP_DUMP_ID
}

UserProcessKill() {
    copylogs
    stopServer
    timestamp
}

restartServer() {

    stopServer

    log "Waiting For TCP Port To Be Free From TIME_WAIT"

	TIMEWAITCOUNT=`sudo netstat -anp | grep -Ew "$PD_PORT|$NV_REST_PORT" | wc -l`

	echo $TIMEWAITCOUNT

	while [[ $TIMEWAITCOUNT -gt 0 ]]
	do
        	echo $TIMEWAITCOUNT
	        TIMEWAITCOUNT=`sudo netstat -anp | grep -Ew "$PD_PORT|$NV_REST_PORT" | wc -l`
	        echo "Shutdown Is Still In Progress...i"
		sleep 10
	done


    log "Starting netvertex server"
    cd $NV_HOME/bin; sh netvertex.sh

    log "Run Config Test for Policy Designer"
    cd $PD_HOME/../bin; sh catalina.sh start

    log "Starting policy designer"
    cd $PD_HOME/../bin; sh catalina.sh start

    sleep 20

    viewMyServerProcesses restartServerRunningProcess

    sleep 100

    log "Starting process with process id $restartServerRunningProcess"

    checkEngineStarted
    checkPolicyDesinerStarted
}

checkPolicyDesinerStarted() {
    cd $TTSBIN
    java -cp automationtools.jar  com.sterlite.pcc.serverstatus.NvsmxStatus $OSIP $PD_PORT

    if [ $? = 1 ] ; then
        echo "Policy Designer not properly restart $USR" >> $AUTOMATION_REPORT_PATH/error.log
        UserProcessKill
        exit 1
    fi
}

checkEngineStarted() {
    cd $TTSBIN
    java -cp automationtools.jar  com.sterlite.pcc.serverstatus.ServerStatus $OSIP $NV_REST_PORT

    if [ $? = 1 ] ; then
        echo "NetVertex server not properly restart $USR" >> $AUTOMATION_REPORT_PATH/error.log
        UserProcessKill
        exit 1
    fi

}

stopServer() {

    log "Shutting down netvertex server"
    cd $NV_HOME/bin; sh cli.sh -c shutdown
    sleep 30
    cd $NV_HOME/bin; sh cli.sh -c shutdown abort

    log "Shutting down policy designer"
    cd $PD_HOME/../bin; sh catalina.sh stop 60 -force
    cd $PD_HOME/../bin;rm -rf ../work/*

    killServers

}


viewMyServerProcesses() {

    local __serverProcess=$1

    local process=`jps | grep -e EliteNetVertexServer -e Bootstrap | awk '{print $1}'`


    if [ -z "$process" ]
    then
        eval $__serverProcess=""
        log "No running processes"
        return;
    else
        log "Running processes:"
        log "`xargs pwdx $process`"
    fi

    local workspaceProcesses=`pwdx $process | grep ${WORKSPACE}/`

    if [ -z "$workspaceProcesses" ]
    then
        log "No running processes from ${WORKSPACE}"
        eval $__serverProcess=""
        return;
    else
        log "Running processes from ${WORKSPACE}:"
        log "$workspaceProcesses"
    fi

    local result="`pwdx $process | grep ${WORKSPACE} | awk -F ":" '{print $1}'`"
    log "$result"
    eval $__serverProcess="'$result'"
}


killServers() {

    viewMyServerProcesses serverProcesses

    log "$serverProcesses"

    if [ -z "$serverProcesses" ]
    then
        log "Noting to kill from ${WORKSPACE}"
        return;
    else
        log "killing processes from ${WORKSPACE}:"
        log "$serverProcesses"
    fi

    kill -9 $serverProcesses
}
