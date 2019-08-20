#!/bin/bash
. ./server.properties

source ./log-utils.sh
source ./utils.sh
source ./server-utils.sh
source ./pcrf-diameter.sh
source ./pcrf-radius.sh
source ./pcrf-sy.sh
source ./pcrf-rx-wsdl.sh
source ./ocs-radius.sh
source ./ocs-diameter.sh
source ./ocs-ro.sh
source ./pcc-diameter.sh
source ./pcc-radius.sh
source ./rest.sh
source ./web-service.sh
source ./global-config.sh

ORACLE='false';
VOLTDB='false';
USR=$osusrname
OSIP=$osip
OSPORT=$osport
USERHOMEDIR=${WORKSPACE}/installation/$USR
CHECKEDVERSION=$checkedversion
CHECKEDREVISION=$checkedrevision
TESTRUNID=$testrunid
ORACLE=$oracle
VOLTDB=$voltdb
CSVHOME=$USERHOMEDIR/'source'/csv
TTSBIN=$USERHOMEDIR/tts/bin
SY_TTS=$USERHOMEDIR/sytts/bin
INPUT_DATA=$TTSBIN/input_data
AUTOMATION_HOME=${WORKSPACE}
AUTOMATION_REPORT_PATH=$AUTOMATION_HOME/automation_report
NV_HOME=${netvertex_home};
PD_HOME=${pd_home};
JAVA_HOME=${java_home}
LOGS_AND_PCAP_DIR=${AUTOMATION_REPORT_PATH}/$USR
NV_REST_PORT=${nvrestport}
PD_PORT=${pdport}

RX_Wsdl='false';

PCRF_Diameter='false';
PCRF_Radius='false';
PCRF_Sy='false';

OCS_Diameter='false';
OCS_Radius='false';
OCS_Ro='false';

PCC_Diameter='false';
PCC_Radius='false';

REST='false';
WEBSERVICE='false';

JOB_NAME='true';
SERVICES='true';


timestamp


export PATH=$JAVA_HOME/bin:$PATH:$HOME/.local/bin:$HOME/bin
export JAVA_HOME

for ARGUMENT in "$@"
do
    ATTRIBUTE=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)
    case "$ATTRIBUTE" in
            AUTOMATION_HOME)           AUTOMATION_HOME=${VALUE} ;;
            PCRF_Diameter)             PCRF_Diameter=${VALUE} ;;
            RX_Wsdl)                    RX_Wsdl=${VALUE} ;;
            PCRF_Radius)               PCRF_Radius=${VALUE} ;;
	    PCRF_Sy)               PCRF_Sy=${VALUE} ;;
            PCC_Diameter)              PCC_Diameter=${VALUE} ;;
            PCC_Radius)                PCC_Radius=${VALUE} ;;
            OCS_Diameter)              OCS_Diameter=${VALUE} ;;
            OCS_Radius)                OCS_Radius=${VALUE} ;;
            OCS_Ro)                    OCS_Ro=${VALUE} ;;
            rest)                      REST=${VALUE} ;;
            webservice)                 WEBSERVICE=${VALUE} ;;
            oracle)                     ORACLE=${VALUE} ;;
            voltdb)                     VOLTDB=${VALUE} ;;
            job_name)                   JOB_NAME=${VALUE} ;;
            *)                  nverror "Invalid Argument Received : $ARGUMENT " ;;
    esac
done


echo -en "########################################################################################################################################\n"
echo -en "Executing Script using following parameters :\n"
echo -en "########################################################################################################################################\n\n"
echo "User=$USR";
echo "Automation Server IP=$OSIP";
echo "Automation Server =$OSPORT";
echo "ORACLE=$ORACLE";
echo "VOLTDB=$VOLTDB";
echo -en "########################################################################################################################################\n\n\n\n"

echo `pwd`

find ../jmx/rest -type f -name \*.csv -exec cp {} $CSVHOME \;
find ../jmx/rest -type f -name \*.json -exec cp {} $TTSBIN \;
cp ../jmx/rest/rest_automation.jmx $TTSBIN
cp ../jmx/PCRF/* $TTSBIN
cp ../jmx/PCC/* $TTSBIN
cp ../jmx/OCS/* $TTSBIN
cp ../jmx/sy/* $SY_TTS
cp ../jmx/global_configurations/PCC_Global_Configurations.jmx $TTSBIN
cp ../jmx/voltdb/PCC_VoltDB.jmx $TTSBIN
cp ../jmx/web_services/* $TTSBIN
cp ../jmx/web_services/data/* $TTSBIN


cd $CSVHOME
head -1 header.csv > $TTSBIN/rest_automation.csv
tail -q -n +2 *.csv | cut -d, -f1 --complement | awk -F, '{$1=++i FS $1;}1' OFS=, >> $TTSBIN/rest_automation.csv

mkdir -p $LOGS_AND_PCAP_DIR
cd $TTSBIN

startTcpDump

############################################################################################################################
# Function declaration starts
############################################################################################################################
copyCsvForReporting() {
    cp -nR $TTSBIN/*.csv $USERHOMEDIR/logs/automation_report/csv
    cp -nR $SY_TTS/*.csv $USERHOMEDIR/logs/automation_report/csv
}

moveLogsForReporting() {
	curpath=`pwd`
	cd $USERHOMEDIR/logs
	ls | grep -v automation_report | xargs mv -t automation_report
	cd $curpath
}


ForceTimeOutSec() {
    time_sec=$1
    cmd="/bin/sh -c \"$2\""

    expect -c "set echo \"-noecho\"; set timeout $time_sec; spawn -noecho $cmd; expect timeout { exit 1 } eof { exit 0 }"
    
    if [ $? = 1 ] ; then
        time=`echo $(($time_sec/3600))h:$(($time_sec%3600/60))m:$(($time_sec%60))s`
	sh stoptest.sh;
        log "FORCEFULL TIMEOUT AFTER ${time}"
	log "Forcefull timeout for $2"  >> $AUTOMATION_REPORT_PATH/error.log
    fi
}

############################################################################################################################
# Function declaration ends
############################################################################################################################


checkPolicyDesinerStarted

executeGlobalConfig

log "SERVICES status=$SERVICES";


if [ "$ORACLE" == "true" ]; then
    if [ $PCRF_Diameter != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executePcrfDiameter
    fi

    if [ $RX_Wsdl != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executeRxWsdl
    fi

    if [ $PCRF_Radius != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executePcrfRadius
    fi
   
    if [ $OCS_Diameter != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executeOcsDiameter
    fi

    if [ $OCS_Radius != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executeOcsRadius
    fi

    if [ $OCS_Ro != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executeOcsRo
    fi

     if [ $WEBSERVICE != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executeWebService
   fi
   
    if [ $PCC_Diameter != 'false' -a $SERVICES != 'false' ]
    then

        restartServer
        cd $TTSBIN
        executePccDiameter
    fi

    if [ $PCC_Radius != 'false' -a $SERVICES != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executePccRadius
    fi   
    if [ $PCRF_Sy != 'false' -a $SERVICES != 'false' ]
        then
            restartServer
            cd $SY_TTS
            executePcrfSy
    fi
    if [ $REST != 'false' ]
    then
        restartServer
        cd $TTSBIN
        executeRest
    fi

fi

#Add logic same done in ORACLE condition when call flow execution test cases added for VoltDB
if [ "$VOLTDB" == "true" -a $SERVICES != 'false' ]
then
    log "########################################################################################################################################\n"
    log "Executing VoltDB Provisioning Call Flow :\n"
    log "########################################################################################################################################\n"
    ForceTimeOutSec 900 "./jmeter.sh -n -t PCC_VoltDB.jmx -l voltdb_automation_execution.csv -LDEBUG -jvoltdb_automation.log" 

    copyCsvForReporting

    log "########################################################################################################################################\n"
    log "Generating Report for VoltDB Call Flow :\n"
    log "########################################################################################################################################\n"
moveLogsForReporting    
java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/voltdb_automation_execution.csv VoltDB_Automation_Result VoltDB $USERHOMEDIR/logs/automation_report/csv/PCC_VoltDB_Mapping.csv
if [ $? = 1 ] ; then
        echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
        UserProcessKill
fi
    if [ $PCRF_Diameter != 'false' -a $SERVICES != 'false' ]
    then

        log "########################################################################################################################################\n"
        log "Executing VoltDB PCRF Diameter Call Flow :\n"
        log "########################################################################################################################################\n"
        ForceTimeOutSec 900 "./jmeter.sh -n -t PCRF_Diameter.jmx -l pcrf_diameter_automation_execution.csv -LDEBUG -jpcrf_diameter_automation.log -Jvoltdb=true"
    
        copyCsvForReporting

        log "########################################################################################################################################\n"
        log "Generating Report for VoltDB PCRF Diameter Call Flow :\n"
        log "########################################################################################################################################\n"
moveLogsForReporting        
java -jar reportgentool.jar -diameter $USERHOMEDIR/logs/automation_report $AUTOMATION_REPORT_PATH $USR $USERHOMEDIR/logs/automation_report/csv/pcrf_diameter_automation_execution.csv PCRF_Diameter_Automation_Result PCRF $USERHOMEDIR/logs/automation_report/csv/PCRF_Diameter_Mapping.csv
if [ $? = 1 ] ; then
        echo "Reportgen tool jar exited with error status for $USR" >> $AUTOMATION_REPORT_PATH/error.log
        UserProcessKill
fi
    fi
fi
log "########################################################################################################################################\n"
log "Execution completed for $USR:\n"
log "########################################################################################################################################\n"
UserProcessKill
copylogs
timestamp
