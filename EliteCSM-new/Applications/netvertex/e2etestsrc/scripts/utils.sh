#!/usr/bin/env bash

source ./log-utils.sh

nverror() {
    log "Exit With Error : $*"
	exit 1
}

get_set_random_port() {
	MyVariableFile=$1;
	MyVariableName=$2;
	MySeperator=$3;
	if [ -z $3 ]
		then
		MySeperator=':'
	fi
	IsNotFreePort="True";

	while [[ ! -z $IsNotFreePort ]]; do
    		RandomPort=$((( RANDOM % 64014 ) + 1521 ))
    		IsNotFreePort=$(netstat -anp | grep $RandomPort)
	done
	if [ ! -z $MyVariableName ]
		then
		sed -i "s/\($MyVariableName$MySeperator\).*/\1$RandomPort/" $MyVariableFile;
	fi
	echo $RandomPort
}


set_up_variables_for_installation() {

	#setup specific variables
	USRNAME=$1
	DB_USRNAME=$1
	USERHOMEDIR=$2
    DEPLOYMENT_MODE=$3
	SCRIPTPATH=$4
	if [ -z $SCRIPTPATH ]
		then
		SCRIPTPATH=`pwd`
	fi
	SM_PORT=`get_set_random_port $SCRIPTPATH/server.properties pdport =`
	SM_SHUTDOWN_PORT=`get_set_random_port $SCRIPTPATH/server.properties pdshutdownport =`
	NV_ADMIN_PORT=`get_set_random_port $SCRIPTPATH/server.properties nvadminport =`
	NV_REST_PORT=`get_set_random_port $SCRIPTPATH/server.properties nvrestport =`
	JMX_REMOTE_PORT=`get_set_random_port $SCRIPTPATH/server.properties jmxremoteport =`
	DIAMETER_PORT=`get_set_random_port $SCRIPTPATH/server.properties diameterport =`
    RADIUS_PORT=`get_set_random_port $SCRIPTPATH/server.properties radiusport =`
    VOLTDB_PORT=`get_set_random_port $SCRIPTPATH/server.properties voltdbport =`

	#set variables in server.properties
	sed -i -e "s|\(usrname=\).*|\1$USRNAME|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(usrhome=\).*|\1$USERHOMEDIR|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(osusrname=\).*|\1$USRNAME|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(dbname=\).*|\1$DB_USRNAME|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(osip=\).*|\1$LOCALIP|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(osport=\).*|\1$SM_PORT|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(testrunid=\).*|\1$TEST_RUN_ID|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(oracle=\).*|\1$ORACLE|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(voltdb=\).*|\1$VOLTDB|" $SCRIPTPATH/server.properties;
	sed -i -e "s|\(deployment=\).*|\1$DEPLOYMENT|" $SCRIPTPATH/server.properties;
	#

	#set up global variables
	sed -i -e "s|\(server_rest_port::\).*|\1$NV_REST_PORT|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(smserverport::\).*|\1$SM_PORT|" $SCRIPTPATH/global.variables;
	if [ "$VOLTDB" == "true" ] ; then
        sed -i -e "s|\(spr_db_url::\).*|\1jdbc:voltdb://$LOCALIP:$VOLTDB_PORT|" $SCRIPTPATH/global.variables;
        sed -i -e "s|\(spr_db_username::\).*|\1voltdb|" $SCRIPTPATH/global.variables;
        sed -i -e "s|\(spr_db_password::\).*|\1voltdb|" $SCRIPTPATH/global.variables;
        sed -i -e "s|\(spr_db_driver::\).*|\1org.voltdb.jdbc.Driver|" $SCRIPTPATH/global.variables;
    else
        sed -i -e "s|\(spr_db_url::\).*|\1jdbc:oracle:thin:@${DB_SERVER_IP}:1521/${ORACLE_SID}|" $SCRIPTPATH/global.variables;
        sed -i -e "s|\(spr_db_username::\).*|\1$DB_USRNAME|" $SCRIPTPATH/global.variables;
        sed -i -e "s|\(spr_db_password::\).*|\1$DB_USRNAME|" $SCRIPTPATH/global.variables;
        sed -i -e "s|\(spr_db_driver::\).*|\1oracle.jdbc.driver.OracleDriver|" $SCRIPTPATH/global.variables;
    fi
	SY_PORT=`get_set_random_port`
	sed -i -e "s|\(syport::\).*|\1$SY_PORT|" $SCRIPTPATH/global.variables;

	sed -i -e "s|\(diameterport::\).*|\1$DIAMETER_PORT|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(adminport::\).*|\1$NV_ADMIN_PORT|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(nvuser::\).*|\1$USRNAME|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(serverinstancename::\).*|\1$USRNAME|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(nvhome::\).*|\1$USERHOMEDIR/server|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(pdhome::\).*|\1$USERHOMEDIR/netvertexsm/sm|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(smshutdownport::\).*|\1$SM_SHUTDOWN_PORT|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(tomcat_home::\).*|\1$USERHOMEDIR/netvertexsm|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(radiusport::\).*|\1$RADIUS_PORT|" $SCRIPTPATH/global.variables;
	JMETER_PORT=`get_set_random_port`
	sed -i -e "s|\(jmeterport::\).*|\1$JMETER_PORT|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(logpath::\).*|\1$USERHOMEDIR\/logs|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(dbusername::\).*|\1$DB_USRNAME|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(dbpassword::\).*|\1$DB_USRNAME|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(sp_dbusername::\).*|\1$DB_USRNAME|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(sp_dbpassword::\).*|\1$DB_USRNAME|" $SCRIPTPATH/global.variables;
	sed -i -e "s|\(deployment::\).*|\1$DEPLOYMENT|" $SCRIPTPATH/global.variables;
    sed -i -e "s|\(deployment_mode::\).*|\1$DEPLOYMENT_MODE|" $SCRIPTPATH/global.variables;
	#
}

appendCsv() {

    SOURCE=$1;
    DESTINATION=$2;


    sed '1d' $SOURCE >> $DESTINATION
}

stopTail() {
    process=`ps -ef | grep -e "tail -f" | grep ${WORKSPACE}/ |  awk '{print $2}'`

    if [ -z "$process" ]
    then
        log "No tail running processes from ${WORKSPACE}"
        return;
    else
        log "Running tail processes from ${WORKSPACE}:"
        log "`pwdx $process`"
        log "$process"
    fi

    log "Killing tail processes from ${WORKSPACE}:"
    serverProcess=`echo $process | xargs kill -9`

}
