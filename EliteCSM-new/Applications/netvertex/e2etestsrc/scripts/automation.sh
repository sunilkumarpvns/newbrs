#!/bin/bash
#./automation.sh ORACLE_HOME=/opt/u01/app/oracle/product/12c/db_1 ORACLE_SID=cdb DB_SERVER_IP=192.168.2.132 ORACLE=true POSTGRES=false VOLTDB=false SINGLENODE=false SINGLENODEEXECUTION=false GX=true RESTWEB=true RADIUS=true GY=true RO=true

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

SINGLENODE=false
SINGLENODEEXECUTION=false
PCRF_DIA=true
RESTWEB=true
PCC=true
OCS=true
RX_WSDL=false

PCRF_DIA=false
PCRF_RAD=false
RX_WSDL=false
OCS_DIA=false
OCS_RAD=false
OCS_R0=false
PCC_DIA=false
PCC_RAD=false
RESTWEB=false
PCRF_SY=false

############################
ORACLE=true
POSTGRES=false
VOLTDB=false
AUTOMATION_HOME=${WORKSPACE}

################## SOFTWARE SOURCE IP #################################

SOFTWARE_SOURCE_IP=192.168.2.137
SOFTWARE_SOURCE_OSUSER=share
SOFTWARE_SOURCE_PWD=share
SOFTWARE_PATH=/eliteaaa/share/nvautomation/softwares

################## DOCKER OR STANDALONE ###############################
DEPLOYMENT=standalone

######################### USER AND FOLDER CONVENTION ######################
#CATEGORY
SOURCE_BRANCH=$(echo $gitlabSourceBranch | sed -e "s/\-/\_/g")
echo $SOURCE_BRANCH
REST_USER=retrunk_${SOURCE_BRANCH}
PCRF_DIA_USER=pcrfdia_${SOURCE_BRANCH}
RX_WSDL_DIA_USER=rx_wsdl_${SOURCE_BRANCH}
PCRF_RAD_USER=pcrfrad_${SOURCE_BRANCH}
PCC_DIA_USER=pccdia_${SOURCE_BRANCH}
PCC_RAD_USER=pccrad_${SOURCE_BRANCH}
OCS_DIA_USER=ocsdia_${SOURCE_BRANCH}
OCS_RAD_USER=ocsrad_${SOURCE_BRANCH}
OCS_RO_USER=ocsro_${SOURCE_BRANCH}
PCRF_SY_USER=pcrfsy_${SOURCE_BRANCH}

#Functions start
start_time=`date +%s`
log "start time : `date`"


#Functions End

#Verifiy that script is executed using Root user or not.
#echo -en "****************************Root User Check********************************************\n"
if [ `id|cut -c5-11` != "0(root)" ] ; then
  nverror 'You must run this script as Root user'
fi
#echo -en "***********************Root User Check Completed**************************************\n"
for ARGUMENT in "$@"
do
    ATTRIBUTE=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)
    case "$ATTRIBUTE" in
            SINGLENODE)		        SINGLENODE=${VALUE} ;;
			ORACLE_HOME)   	        ORACLE_HOME=${VALUE} ;;
	    	ORACLE_SID)    	        ORACLE_SID=${VALUE} ;;
			AUTOMATION_HOME)        AUTOMATION_HOME=${VALUE} ;;
	    	DB_SERVER_IP)           DB_SERVER_IP=${VALUE} ;;
			ORACLE)                 ORACLE=${VALUE} ;;
	    	VOLTDB)                 VOLTDB=${VALUE} ;;
	    	POSTGRES) 	            POSTGRES=${VALUE};;
	    	TEST_RUN_ID)            TEST_RUN_ID=${VALUE} ;;
			PCRF_DIA)   		    PCRF_DIA=${VALUE} ;;
			PCRF_RAD)   		    PCRF_RAD=${VALUE} ;;
            RX_WSDL)   		        RX_WSDL=${VALUE} ;;
            OCS_DIA)         	    OCS_DIA=${VALUE} ;;
            OCS_RAD)         	    OCS_RAD=${VALUE} ;;
            OCS_RO)         	    OCS_RO=${VALUE} ;;
            PCC_DIA)     		    PCC_DIA=${VALUE} ;;
            PCC_RAD)     		    PCC_RAD=${VALUE} ;;
	    PCRF_SY)     		    PCRF_SY=${VALUE} ;;
            RESTWEB)    	        RESTWEB=${VALUE} ;;
            DEPLOYMENT)    	        DEPLOYMENT=${VALUE} ;;
            SOFTWARE_SOURCE_IP)     SOFTWARE_SOURCE_IP=${VALUE} ;;
            SOFTWARE_SOURCE_OSUSER) SOFTWARE_SOURCE_OSUSER=${VALUE} ;;
            SOFTWARE_SOURCE_PWD)    SOFTWARE_SOURCE_PWD=${VALUE} ;;
            SOFTWARE_PATH)          SOFTWARE_PATH=${VALUE} ;;
            *)			nverror "Invalid Argument Received : $ARGUMENT " ;;
    esac
done
if [ "$DEPLOYMENT" == "docker" ]
then
#CATEGORY
AUTOMATION_HOME=${WORKSPACE}
REST_USER=retrunk_${SOURCE_BRANCH}_docker
PCRF_DIA_USER=pcrfdia_${SOURCE_BRANCH}_docker
RX_WSDL_DIA_USER=rx_wsdl_${SOURCE_BRANCH}_docker
PCRF_RAR_USER=pcrfrad_${SOURCE_BRANCH}_docker
PCC_DIA_USER=pccdia_${SOURCE_BRANCH}_docker
PCC_RAD_USER=pccrad_${SOURCE_BRANCH}_docker
OCS_DIA_USER=ocsdia_${SOURCE_BRANCH}_docker
OCS_RAD_USER=ocsrad_${SOURCE_BRANCH}_docker
OCS_RO_USER=ocsro_${SOURCE_BRANCH}_docker
PCRF_SY_USER=pcrfsy_${SOURCE_BRANCH}_docker
fi


#Create New Report directory locally <same as previous>
CURRENTPATH=`pwd`
rm -rf $AUTOMATION_HOME/automation_report
rm -rf ../softwares
rm -rf ../source
mkdir -p $AUTOMATION_HOME/automation_report
cd $AUTOMATION_HOME/automation_report
AUTOMATION_REPORT_PATH=$AUTOMATION_HOME/automation_report
AUTOMATION_EXECUTION_LOGS=$AUTOMATION_REPORT_PATH/automation_execution_logs
mkdir -p $AUTOMATION_EXECUTION_LOGS
cd $CURRENTPATH
sed -i -e "s|\(automation_report_path=\).*|\1$AUTOMATION_REPORT_PATH|" server.properties;
sed -i -e "s|\(automation_home=\).*|\1$AUTOMATION_HOME|" server.properties;
mkdir ../softwares
mkdir ../source


#copy binaries to source directory
cp ${WORKSPACE}/Applications/nvsmx/build/*.tar.gz ../source
cp ${WORKSPACE}/Applications/netvertex/build/*.tar.gz ../source
rm ../source/*-upgrade-*
#

#get softwares from share server
export PATH=/usr/local/bin:/usr/local/sbin:/usr/bin:/usr/sbin:/bin:/sbin:$PATH:.
export SSHPASS=$SOFTWARE_SOURCE_PWD
sshpass -e sftp -o "StrictHostKeyChecking no" $SOFTWARE_SOURCE_OSUSER@$SOFTWARE_SOURCE_IP << EOF
cd $SOFTWARE_PATH
get -P * ../softwares
bye
EOF
#

if [ -z "$(ls ../softwares)" ]; then
   log "software directory not empty"
else
   log "software directory is empty"
fi



#Comman Variables for all setup
LOCALIP=`hostname -I | awk '{print $1}'`;
ORACLE_HOME=$ORACLE_HOME
ORACLE_SID=$ORACLE_SID
DB_SERVER_IP=$DB_SERVER_IP
TESTRUN_ID=$TEST_RUN_ID
ORACLE=$ORACLE
VOLTDB=$VOLTDB
POSTGRES=$POSTGRES
#

#Set global.variables for DB related variables and ip variables
sed -i -e "s|\(dbserverip=\).*|\1$DB_SERVER_IP|" $CURRENTPATH/server.properties;
sed -i -e "s|\(dbserversid::\).*|\1$ORACLE_SID|" $CURRENTPATH/global.variables;
sed -i -e "s|\(oraclesid=\).*|\1$ORACLE_SID|" $CURRENTPATH/server.properties;
sed -i -e "s|\(oraclehome::\).*|\1$ORACLE_HOME|" $CURRENTPATH/global.variables;
sed -i -e "s|\(oraclehome=\).*|\1$ORACLE_HOME|" $CURRENTPATH/server.properties;
sed -i -e "s|\(dbserverip_for_sp_interface::\).*|\1$DB_SERVER_IP|" $CURRENTPATH/global.variables;
sed -i -e "s|\(serverip::\).*|\1$LOCALIP|" $CURRENTPATH/global.variables;
sed -i -e "s|\(dbserverip::\).*|\1$DB_SERVER_IP|" $CURRENTPATH/global.variables;
sed -i -e "s|\(sy_serverip::\).*|\1$LOCALIP|" $CURRENTPATH/global.variables;
sed -i -e "s|\(jmeterip::\).*|\1$LOCALIP|" $CURRENTPATH/global.variables;
#

chmod 777 ../scripts/install-apps.sh
chmod 777 ../scripts/execution.sh
chmod 777 ../scripts/cleanup.sh


INSTALLATION_LOG_SUFIX="_installation.log"

#Create Setup Data directory for multiple node
rm -rf ../../multiple_node_setup_data
mkdir ../../multiple_node_setup_data

#installation of multiple setup
 if [ "$PCRF_DIA" == "true" ]
 then
    setupPcrfDia
 fi

 if [ "$PCRF_RAD" == "true" ]
 then
    setupPcrfRadius
 fi

if [ "$RX_WSDL" == "true" ]
 then
    setupRxWsdl
 fi

 if [ "$RESTWEB" == "true" ]
 then
    setupRest
 fi

 if [ "$PCC_RAD" == "true" ]
 then
    setupPccRadius
 fi

 if [ "$PCC_DIA" == "true" ]
 then
    setupPccDiameter
 fi

 if [ "$OCS_DIA" == "true" ]
 then
    setupOcsDiameter
 fi

 if [ "$OCS_RAD" == "true" ]
 then
    setupOcsRadius
 fi

 if [ "$OCS_RO" == "true" ]
 then
    setupOcsRo
 fi
 if [ "$PCRF_SY" == "true" ]
 then
    setupPcrfSy
 fi
wait
log "-------------------------"
log "Installations Completed"

#execution in multiple setup
 EXECUTION_LOG_SUFIX="_execution.log"
 if [ "$PCRF_DIA" == "true" ]
 then
    cd ../../multiple_node_setup_data/$PCRF_DIA_USER/scripts
    log "Executing PCRF Diameter"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME PCRF_Diameter=true >> $AUTOMATION_EXECUTION_LOGS/$PCRF_DIA_USER$EXECUTION_LOG_SUFIX 2>&1 &
 fi

 if [ "$PCRF_RAD" == "true" ]
 then
    cd ../../multiple_node_setup_data/$PCRF_RAD_USER/scripts
    log "Executing PCRF Radius"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME PCRF_Radius=true >> $AUTOMATION_EXECUTION_LOGS/$PCRF_RAD_USER$EXECUTION_LOG_SUFIX 2>&1 &
 fi

if [ "$RX_WSDL" == "true" ]
 then
    cd ../../multiple_node_setup_data/$RX_WSDL_DIA_USER/scripts
    log "Executing Rx And WSDL Scenario"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME RX_Wsdl=true >> $AUTOMATION_EXECUTION_LOGS/$PCRF_DIA_USER$EXECUTION_LOG_SUFIX 2>&1 &
    cd $CURRENTPATH
 fi

 if [ "$RESTWEB" == "true" ]
 then
    cd ../../multiple_node_setup_data/$REST_USER/scripts
    log "Executing rest and webservice"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME rest=true webservice=true >> $AUTOMATION_EXECUTION_LOGS/$REST_USER$EXECUTION_LOG_SUFIX 2>&1 &
            #./execution.sh gy=false sy=false radius=false rest=false ro=false webservice=false diameter=false &
 fi

 if [ "$PCC_DIA" == "true" ]
 then
    cd ../../multiple_node_setup_data/$PCC_DIA_USER/scripts
    log "Executing PCC Diameter"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME PCC_Diameter=true >> $AUTOMATION_EXECUTION_LOGS/$PCC_DIA_USER$EXECUTION_LOG_SUFIX 2>&1 &
 fi

 if [ "$PCC_RAD" == "true" ]
 then
    cd ../../multiple_node_setup_data/$PCC_RAD_USER/scripts
    log "Executing PCC Radius"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME PCC_Radius=true >> $AUTOMATION_EXECUTION_LOGS/$PCC_RAD_USER$EXECUTION_LOG_SUFIX 2>&1 &
 fi

 if [ "$OCS_DIA" == "true" ]
 then
    cd ../../multiple_node_setup_data/$OCS_DIA_USER/scripts
    log "Executing OCS Diameter"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME OCS_Diameter=true >> $AUTOMATION_EXECUTION_LOGS/$OCS_DIA_USER$EXECUTION_LOG_SUFIX 2>&1 &
 fi

 if [ "$OCS_RAD" == "true" ]
 then

    cd ../../multiple_node_setup_data/$OCS_RAD_USER/scripts
    log "Executing OCS Radius"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME OCS_Radius=true >> $AUTOMATION_EXECUTION_LOGS/$OCS_RAD_USER$EXECUTION_LOG_SUFIX 2>&1 &

 fi

 if [ "$OCS_RO" == "true" ]
 then
    cd ../../multiple_node_setup_data/$OCS_RO_USER/scripts
    log "Executing OCS Ro"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME OCS_Ro=true >> $AUTOMATION_EXECUTION_LOGS/$OCS_RO_USER$EXECUTION_LOG_SUFIX 2>&1 &
 fi

 if [ "$PCRF_SY" == "true" ]
 then
    cd ../../multiple_node_setup_data/$PCRF_SY_USER/scripts
    log "Executing PCRF SY"
    bash -x execution.sh AUTOMATION_HOME=$AUTOMATION_HOME PCRF_Sy=true >> $AUTOMATION_EXECUTION_LOGS/$PCRF_SY_USER$EXECUTION_LOG_SUFIX 2>&1 &
 fi

wait



log "-------------------------"
log "Execution Completed"
log "-------------------------"
if [[ -s $AUTOMATION_REPORT_PATH/error.log ]]
then
	log
	log  "Automation Fail Reasons :"
	log
	cat $AUTOMATION_REPORT_PATH/error.log
fi

end_time=`date +%s`
runtime=$((end_time-start_time))
log "End time : `date`"
log
exec_time=`echo $(($runtime/3600))h:$(($runtime%3600/60))m:$(($runtime%60))s`
log "Total Execution Time : $exec_time"
log
if [[ -s $AUTOMATION_REPORT_PATH/error.log ]]
then 
	log "***Automation Failed***"
	exit 1 
else 
	log "***Automtaion Executed successfully***"
fi
