#!/bin/bash

source ./log-utils.sh
source ./utils.sh
source ./server-utils.sh
source ./voltdb.sh


echo "$( echo $(cd ../ && pwd) )/scripts/server.properties"
. ./server.properties


#Exit function
nverror()
{
        log "\nExit With Error : $@"
        exit 1
}
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
      USRNAME)		USRNAME=${VALUE} ;;
      DB_USRNAME) 	DB_USRNAME=${VALUE} ;;
	    ORACLE_HOME)   	ORACLE_HOME=${VALUE} ;;
	    ORACLE_SID)    	ORACLE_SID=${VALUE} ;;
	    SM_PORT)     	SM_PORT=${VALUE} ;;
	    SM_SHUTDOWN_PORT)   SM_SHUTDOWN_PORT=${VALUE} ;;
	    NV_ADMIN_PORT)    	NV_ADMIN_PORT=${VALUE} ;;
	    NV_REST_PORT)	NV_REST_PORT=${VALUE} ;;
	    JMX_REMOTE_PORT)   	JMX_REMOTE_PORT=${VALUE} ;;	
	    DB_SERVER_IP)    	DB_SERVER_IP=${VALUE} ;;
	    ORACLE)         	ORACLE=${VALUE} ;;
	    VOLTDB)         	VOLTDB=${VALUE} ;;
	    POSTGRES) 	        POSTGRES=${VALUE};;
	    TEST_RUN_ID)    	TEST_RUN_ID=${VALUE} ;;
	    USERHOMEDIR)    	USERHOMEDIR=${VALUE} ;;
            *)			nverror "Invalid Argument Received : $ARGUMENT " ;;
    esac
done

USERHOMEDIR=${WORKSPACE}/installation/$USRNAME

############################################################################################
LOCALIP=`hostname -I | awk '{print $1}'`;
USRNAME=${USRNAME:-$usrname}
USERHOMEDIR=${USERHOMEDIR:-$userhome}
DB_USRNAME=${DB_USRNAME:-$dbname}
ORACLE_HOME=${ORACLE_HOME:-$oraclehome}
ORACLE_SID=${ORACLE_SID:-$oraclesid}
SM_PORT=${SM_PORT:-$pdport}
SM_SHUTDOWN_PORT=${SM_SHUTDOWN_PORT:-$pdshutdownport}
NV_ADMIN_PORT=${NV_ADMIN_PORT:-$nvadminport}
NV_REST_PORT=${NV_REST_PORT:-$nvrestport}
JMX_REMOTE_PORT=${JMX_REMOTE_PORT:-$jmxremoteport}
USER_CURRENT_PATH=`pwd`
DB_SERVER_IP=${DB_SERVER_IP:-$dbserverip}
TESTRUN_ID=${TEST_RUN_ID:-$testrunid}
ORACLE=${ORACLE:-$oracle}
VOLTDB=${VOLTDB:-$voltdb}
POSTGRES=${POSTGRES:-$postgres}
DIAMETER_PORT=${DIAMETER_PORT:-$diameterport}
RADIUS_PORT=${RADIUS_PORT:-$radiusport}
VOLTDB_PORT=${VOLTDB_PORT:-$voltdbport}
###########################################################################################

log "###############################################################################################################\n"
log "Executing Script using following parameters :\n"
log "###############################################################################################################\n\n"
log "USRNAME=$USRNAME";
log "USERHOMEDIR=$USERHOMEDIR";
log "DB_USRNAME=$DB_USRNAME";
log "ORACLE_HOME=$ORACLE_HOME";
log "ORACLE_SID=$ORACLE_SID";
log "SM_PORT=$SM_PORT";
log "SM_SHUTDOWN_PORT=$SM_SHUTDOWN_PORT";
log "NV_ADMIN_PORT=$NV_ADMIN_PORT";
log "NV_REST_PORT=$NV_REST_PORT"
log "JMX_REMOTE_PORT=$JMX_REMOTE_PORT";
log "DB_SERVER_IP=$DB_SERVER_IP";
log "ORACLE=$ORACLE";
log "VOLTDB=$VOLTDB";
log "POSTGRES=$POSTGRES";
log "DIAMETER_PORT=$DIAMETER_PORT";
log "RADIUS_PORT=$RADIUS_PORT";
log "USER_CURRENT_PATH=$USER_CURRENT_PATH";
log "VOLTDB_PORT=$VOLTDB_PORT";
log "###############################################################################################################\n\n\n\n"
log "###############################################################################################################\n"
log "1. Copying Build \n"
log "###############################################################################################################\n\n"
pwd
DESTINATION=$USER_CURRENT_PATH
cp ../source/* $DESTINATION

#log "###############################################################################################################\n\n\n\n"
#echo -en "###############################################################################################################\n"
#echo -en "2. Deleting User if Already Exists \n"
#echo -en "###############################################################################################################\n\n"
#sleep 1
#if [ -z $USRNAME ]
#then
#nverror 'USRNAME variable not set'
#fi
#if [ ! -z $USRNAME ]
#then
#pkill -9 -u `id -u $USRNAME`
#fi
#sleep 1
#rm -rf /tmp/hsperfdata_$USRNAME
#userdel -rf $USRNAME
#if [ ! -z $USRNAME ]
#then
#rm -rf /home/$USRNAME
#fi
#echo -en "###############################################################################################################\n\n\n\n"
#echo -en "###############################################################################################################\n"
#echo -en "3. Deleting /opt/trunk_build folder"
#echo -en "###############################################################################################################\n\n"
#rm -rf /opt/trunk_build
#mkdir -p /opt/trunk_build
#sleep 1
#echo -en "###############################################################################################################\n\n\n\n"
#echo -en "###############################################################################################################\n"
#echo -en "4. Creating User : $USRNAME \n"
#echo -en "###############################################################################################################\n\n"
##echo -en "\n Verify User is deleted or not !!!! \n"
#CheckNVUSER=`cat /etc/passwd | grep -w "$USRNAME"`
#if [ $? == '1' ]; then
#        echo -e "User not present"
#                echo "Creating user with USRNAME : $USRNAME"
#                useradd -m -s /bin/bash $USRNAME
#                echo -e "$USRNAME\n$USRNAME\n" | sudo passwd $USRNAME
#                cat /etc/passwd | tail -1
#else
#   nverror "3 Problem while creating new user : Previous user $USRNAME delete failed"
#fi
#echo -en "User : $USRNAME created successfully \n"
#echo -en "###############################################################################################################\n\n\n\n"
#CURRENTPATH=$USER_CURRENT_PATH
#USERHOMEDIR=`grep -w "$USRNAME" /etc/passwd|cut -f6 -d":"`
#chown -R $USRNAME:$USRNAME $USERHOMEDIR
CURRENTPATH=$USER_CURRENT_PATH
cd $CURRENTPATH

USERHOMEDIR=${WORKSPACE}/installation/$USRNAME
#rm -f *upgrade*.tar.gz
log "###############################################################################################################\n"
log "5. Preparing Directory Structure and Installing NetVertex and Policy Designer \n"
log "###############################################################################################################\n\n"
log "**********************Final list of Files****************************************\n"
echo "`ls *.tar.gz`"
log "****************Creating Required directories in $USERHOMEDIR********************\n"
mkdir -p "$USERHOMEDIR"/{server,'source',java,logs}
mkdir -p "$USERHOMEDIR"/'source'/{sm,java,server,tts,sytts,csv,report,voltdb}
log "*********************************************************************************\n"
log "****************Copying Final list of files to source directory******************\n"
mv $CURRENTPATH/*SM*.tar.gz "$USERHOMEDIR"/'source'/sm
mv $CURRENTPATH/*Netvertex-*.tar.gz "$USERHOMEDIR"/'source'/server
cp $CURRENTPATH/*.jar "$USERHOMEDIR"/'source'/report
cp $CURRENTPATH/report.css "$USERHOMEDIR"/'source'/report
cp $CURRENTPATH/*.js "$USERHOMEDIR"/'source'/report
SOFTWAREDIR=`cd ../softwares/ ; pwd ; cd ../scripts/;`
GLOBAL_DIR=`cd ../scripts/;pwd;`
#cp $SOFTWAREDIR/*tomcat*.tar.gz "$USERHOMEDIR"/'source'/tomcat
cp $GLOBAL_DIR/global.variables "$USERHOMEDIR"/'source'/
cp $GLOBAL_DIR/server.properties "$USERHOMEDIR"/'source'/
cp $SOFTWAREDIR/*jdk*.tar.gz "$USERHOMEDIR"/'source'/java
cp $SOFTWAREDIR/tts.tar.gz "$USERHOMEDIR"/'source'/tts
cp $SOFTWAREDIR/sytts.tar.gz "$USERHOMEDIR"/'source'/sytts
cp $SOFTWAREDIR/*voltdb*.tar.gz "$USERHOMEDIR"/'source'/voltdb
log "*********************************************************************************\n"
##############################################################################################
PD_HOME=$USERHOMEDIR/netvertexsm/sm
NV_HOME=$USERHOMEDIR/server
TOMCAT_HOME=$USERHOMEDIR/netvertexsm
JAVA_HOME=$USERHOMEDIR/java
TTSBIN=$USERHOMEDIR/tts/bin
SY_TTS=$USERHOMEDIR/sytts/bin
LOGDIR=$USERHOMEDIR/logs
##############################################################################################
log "***********Installing NV and PD with Tomcat and Java Environment*****************\n"
cd $USERHOMEDIR/'source'/sm ; tar -xzf *SM*.tar.gz; sh 'install-web-app.sh' $USERHOMEDIR 2>&1; cd ../..;
cd $USERHOMEDIR/'source'/server ; tar -xzf *Netvertex-*.tar.gz; sh install.sh $NV_HOME 2>&1; cd ../..;
cd $USERHOMEDIR/'source'/java ; tar -xf *jdk*.tar.gz -C $JAVA_HOME  2>&1; cd ../..;
cd $USERHOMEDIR/'source'/tts ; tar -xzf tts.tar.gz -C $USERHOMEDIR 2>&1; cd ../..;
cd $USERHOMEDIR/'source'/sytts ; tar -xzf sytts.tar.gz -C $USERHOMEDIR 2>&1; cd ../..;

cd $USERHOMEDIR/'source'/voltdb ; tar -xzf *voltdb*.tar.gz -C $USERHOMEDIR 2>&1; cd ../..;
mkdir $TTSBIN/input_data;
mkdir $TTSBIN/response_data;
mkdir $TTSBIN/rest_automation_logs;
mkdir -p $LOGDIR/automation_report;
mkdir -p $LOGDIR/automation_report/csv;
mkdir -p $LOGDIR/automation_report/rest_automation_report;
mkdir -p $LOGDIR/automation_report/rest_automation_report/input_data;
mkdir -p $LOGDIR/automation_report/rest_automation_report/response_data;
mkdir -p $LOGDIR/automation_report/rest_automation_report/rest_automation_logs;
cp $USERHOMEDIR/'source'/report/report.css $LOGDIR/automation_report/rest_automation_report;
cp $USERHOMEDIR/'source'/report/report.css $LOGDIR/automation_report;
cp $USERHOMEDIR/'source'/report/*.js $LOGDIR/automation_report/rest_automation_report;
cp $USERHOMEDIR/'source'/report/*.js $LOGDIR/automation_report;
cp $USERHOMEDIR/'source'/report/*.jar $TTSBIN;
cp $USERHOMEDIR/'source'/global.variables $TTSBIN;

cp $USERHOMEDIR/'source'/report/*.jar $SY_TTS;
cp $USERHOMEDIR/'source'/global.variables $SY_TTS;

log "**********************************************************************************\n"
log "###############################################################################################################\n\n\n\n"
##############################################################################################
log "###############################################################################################################\n"
log "6. Setting .bash_profile for user : $USRNAME\n"
log "###############################################################################################################\n\n"
cd $JAVA_HOME;
jdk_name=`ls`;
cd $USERHOMEDIR;
#echo "" >> .bash_profile;
#echo "JAVA_HOME=$JAVA_HOME/$jdk_name" >> .bash_profile;
#echo 'export JAVA_HOME' >> .bash_profile;
#echo 'PATH=$JAVA_HOME/bin:$PATH:$HOME/.local/bin:$HOME/bin' >> .bash_profile;
#echo -n 'export PATH' >> .bash_profile;
#source .bash_profile;
JAVA_HOME=$JAVA_HOME/$jdk_name
export PATH=$JAVA_HOME/bin:$PATH:$HOME/.local/bin:$HOME/bin
export JAVA_HOME
if [ `echo $JAVA_HOME | grep $JAVA_HOME | grep $jdk_name | wc -l` != 1 ] ; then
  nverror '5 Expected JAVA_HOME not set'
fi
log "JAVA_HOME = `echo $JAVA_HOME`\n"
#log "**********************************************************************************\n"
log "###############################################################################################################\n\n\n\n"
##############################################################################################
log "###############################################################################################################\n"
log "7. Connecting Database and Creating Schema for NetVertex. This may take some time\n"
log "###############################################################################################################\n\n"

##############################################################################################
# Functions for Oracle DB :
##############################################################################################

checkOracleUserExistence(){
if [ `ls $PD_HOME/setup/database/fullsetup/*schema*.sql` != "$PD_HOME/setup/database/fullsetup/netvertex-schema.sql" ] ; then
  nverror '6. DB schema file not present : netvertex-schema.sql'
fi
if [ `ls $PD_HOME/setup/database/fullsetup/netvertex.sql` != "$PD_HOME/setup/database/fullsetup/netvertex.sql" ] ; then
  nverror '6. DB schema file not present : netvertex.sql'
fi
SCHEMAFILE=`ls $PD_HOME/setup/database/fullsetup/*schema*.sql`
sed -i "s/netvertex/$DB_USRNAME/g" $SCHEMAFILE

sshpass -e ssh -o "StrictHostKeyChecking no" oracle@$DB_SERVER_IP /bin/bash << STP
. /home/oracle/.bash_profile
cd $ORACLE_HOME 
sqlplus / as sysdba << EOF
drop user $DB_USRNAME cascade;
DROP TABLESPACE $DB_USRNAME INCLUDING CONTENTS AND DATAFILES;
exit
EOF
mkdir /home/oracle/$DB_USRNAME
exit
STP
ORACLEHOME=/home/oracle/$DB_USRNAME;
}

copyOracleSchema(){
export SSHPASS=oracle
sshpass -e sftp -o "StrictHostKeyChecking no" oracle@$DB_SERVER_IP << EOF2
cd $ORACLEHOME
put $USERHOMEDIR/netvertexsm/sm/setup/database/fullsetup/netvertex-schema.sql
put $USERHOMEDIR/netvertexsm/sm/setup/database/fullsetup/netvertex.sql
put $USERHOMEDIR/netvertexsm/sm/setup/database/fullsetup/networkInformation.sql
bye
EOF2
}

executeOracleSchema(){
export SSHPASS='oracle'
sshpass -e ssh -o "StrictHostKeyChecking no" oracle@$DB_SERVER_IP /bin/bash <<STPT
chown oracle:oinstall $ORACLEHOME/*.sql;
. /home/oracle/.bash_profile
cd $ORACLE_HOME
sqlplus / as sysdba << EOF
@$ORACLEHOME/netvertex-schema.sql
exit
EOF
sqlplus / as sysdba << EOF
conn $DB_USRNAME/$DB_USRNAME;
@$ORACLEHOME/netvertex.sql
exit
EOF
sqlplus / as sysdba << EOF
conn $DB_USRNAME/$DB_USRNAME;
@$ORACLEHOME/networkInformation.sql
exit
EOF
exit
STPT
}
##############################################################################################
# End OF Functions for Oracle DB
##############################################################################################
##############################################################################################
# Functions for Postgres DB :
##############################################################################################

checkPostgresUserExistence(){
if [[  `ls $PD_HOME/setup/database/postgres/*schema*.sql` != $PD_HOME/setup/database/postgres/netvertex-schema.sql ]] ; then
  nverror '6. DB schema file not present : netvertex-schema.sql'
fi
if [  `ls $PD_HOME/setup/database/postgres/netvertex.sql` != "$PD_HOME/setup/database/postgres/netvertex.sql"  ] ; then
  nverror '6. DB schema file not present : netvertex.sql'
fi

SCHEMAFILE=`ls $PD_HOME/setup/database/postgres/*schema*.sql`

sed -i "s/netvertex/$DB_USRNAME/g" $SCHEMAFILE
sed -i "s/opt/home/g" $SCHEMAFILE

log "Edit Finish"

sshpass -e ssh -o "StrictHostKeyChecking no" postgres@$DB_SERVER_IP /bin/bash << STP
. /home/postgres/.bash_profile

psql << EOF
DROP SCHEMA $DB_USRNAME CASCADE;
DROP OWNED BY $DB_USRNAME;
DROP TABLESPACE IF EXISTS $DB_USRNAME;
DROP USER IF EXISTS $DB_USRNAME;
DROP ROLE IF EXISTS $DB_USRNAME;
\q
EOF

mkdir /home/postgres/$DB_USRNAME

exit
STP
log "Cleared Postgres"
POSTGRES_HOME=/home/postgres/$DB_USRNAME;

}

copyPostgresSchema(){
export SSHPASS='postgres'
sshpass -e sftp -o "StrictHostKeyChecking no" postgres@$DB_SERVER_IP << EOF2
cd $POSTGRES_HOME
put $USERHOMEDIR/netvertexsm/sm/setup/database/postgres/netvertex-schema.sql
put $USERHOMEDIR/netvertexsm/sm/setup/database/postgres/netvertex.sql
put $USERHOMEDIR/netvertexsm/sm/setup/database/postgres/networkInformation.sql
put $USERHOMEDIR/netvertexsm/sm/setup/database/postgres/pgagent.sql
bye
EOF2
log "Copied Schema"
}

executePostgresSchema(){
export SSHPASS='postgres'
sshpass -e ssh -o "StrictHostKeyChecking no" postgres@$DB_SERVER_IP /bin/bash <<STPT
chown postgres:postgres $POSTGRES_HOME/*.sql;
. /home/postgres/.bash_profile
cd $POSTGRES_HOME
psql -f pgagent.sql
psql -f netvertex-schema.sql

psql << EOF

set role $DB_USRNAME;
set search_path = $DB_USRNAME;

\i $POSTGRES_HOME/netvertex.sql

\q
EOF
psql << EOF
set role $DB_USRNAME;
set search_path = $DB_USRNAMEe;

\i $POSTGRES_HOME/networkInformation.sql
\q
EOF

exit
STPT
}
##############################################################################################
# End OF Functions for Postgres DB
##############################################################################################

##############################################################################################
# Functions for VoltDB
##############################################################################################
setUpVoltDB() {
cd $USERHOMEDIR/voltdb-community-8.0/bin;
./voltadmin shutdown
sleep 5s


log "Initializing VoltDB, Wait for 5second"
./voltdb init --force
sleep 5s
log "Starting VoltDB, Wait for 20second"
./voltdb start --client=${VOLTDB_PORT} > /dev/null 2>&1 &
sleep 20s

log "Uploading voltdb-netvertex.sql to VoltDB Server"
cp ${WORKSPACE}/Applications/nvsmx/setup/database/voltdb/voltdb-netvertex.sql .
log "Uploading NetVertex-PCC-StoredProcedure.jar to VoltDB Server"
cp ${WORKSPACE}/Applications/nvsmx/setup/database/voltdb/NetVertex-PCC-StoredProcedure.jar .

log "Executing voltdb-netvertex.sql on VoltDB Server"
./sqlcmd --port=${VOLTDB_PORT} < voltdb-netvertex.sql
}

##############################################################################################
# End OF Functions for VoltDB
##############################################################################################

if [ "$POSTGRES" == "true" ]; then
    log "###############################################################################################################\n"
    log " Connecting Postgres and Creating Schema for NetVertex. This may take some time\n"
    log "###############################################################################################################\n\n"
    export SSHPASS=postgres
    checkPostgresUserExistence
    copyPostgresSchema
    executePostgresSchema
else
    log "###############################################################################################################\n"
    log " Connecting Oracle and Creating Schema for NetVertex. This may take some time\n"
    log "###############################################################################################################\n\n"
    export SSHPASS=oracle
    checkOracleUserExistence
    copyOracleSchema
    executeOracleSchema
fi

if [ "$VOLTDB" == "true" ] ; then
    log "###############################################################################################################\n"
    log " Connecting VoltDB and Creating Schema for NetVertex. This may take some time\n"
    log "###############################################################################################################\n\n"
    export SSHPASS=voltdb
    setUpVoltDB
fi
##############################################################################################
log "###############################################################################################################\n\n\n\n"
log "###############################################################################################################\n"
log "8. Setting Configuration Files for NetVertex and NetVertexSM \n"
log "###############################################################################################################\n\n"
#chown -R $USRNAME:$USRNAME $USERHOMEDIR
##############################################################################################
APACHELOG=$TOMCAT_HOME/logs
APACHEBIN=$TOMCAT_HOME/bin
APACHEWEBAPPS=$TOMCAT_HOME/webapps
APACHECONF=$TOMCAT_HOME/conf
NVServer=$NV_HOME/bin
log "Setting server.xml for Tomcat \n"
sed -i "s/\(Connector port=\"9091\"\).*/Connector port=\"$SM_PORT\" protocol=\"HTTP\/1\.1\"/" $APACHECONF/server.xml
sed -i "s/\(Server port=\).*/\1\"$SM_SHUTDOWN_PORT\" shutdown=\"SHUTDOWN\">/" $APACHECONF/server.xml
#log "*********************Editing Netvertex.sh*****************************************\n"
## Netvertex admin port for future use
sed -i "s/\(ADMIN_PORT=\).*/\1$NV_ADMIN_PORT/" $NV_HOME/bin/netvertex.sh
sed -i "s/\(REST_PORT=\).*/\1$NV_REST_PORT/" $NV_HOME/bin/netvertex.sh
sed -i "s/\(CS.MSISDN\).*/\1 -Drevalidationtime.delta=0\"/" $NV_HOME/bin/netvertex.sh
#log "**********************************************************************************\n"
###############################################################################################
if [  "$POSTGRES" == "true" ] ; then

echo "driverClassName=org.postgresql.Driver
url=jdbc:postgresql://$DB_SERVER_IP:5432/postgres
USRNAME=$DB_USRNAME
password=$DB_USRNAME
maxIdle=10
validationQueryTimeout=1
testOnBorrow=true
encryptedPassword=false" > $PD_HOME/WEB-INF/database.properties

else

sed -i "s/\(encryptedPassword\).*/\1false/" $PD_HOME/WEB-INF/database.properties
sed -i "s/\(url=jdbc:oracle:thin:@\/\/\).*/\1$DB_SERVER_IP:1521\/$ORACLE_SID/" $PD_HOME/WEB-INF/database.properties
sed -i "s/\(username=\).*/\1$DB_USRNAME/" $PD_HOME/WEB-INF/database.properties
sed -i "s/\(password=\).*/\1$DB_USRNAME/" $PD_HOME/WEB-INF/database.properties

fi


log "SM PORT NETSTAT= `netstat -anp | grep -e $SM_PORT`"
log "ENGINE REST PORT NETSTAT=`netstat -anp | grep -e $NV_REST_PORT`"
log "ADMIN REST PORT=`netstat -anp | grep -e $NV_ADMIN_PORT`"
###############################################################################################
sed -i "/os400=false/ a export JAVA_OPTS=\"-d64 -server -Xms1024M -Xmx2048M -XX:MaxPermSize=256M -Djava.awt.headless=true -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=60 -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError -Xloggc:tomcatgc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -Dcom.sun.management.jmxremote.port=$JMX_REMOTE_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false\"" $APACHEBIN/startup.sh
echo "CATALINA_PID=\"\$CATALINA_BASE/bin/catalina.pid\"" > $APACHEBIN/setenv.sh
###############################################################################################
log "###############################################################################################################\n\n\n\n"
cd $USERHOMEDIR
log "###############################################################################################################\n"
log "9. starting tomcat and netvertex server\n"
log "###############################################################################################################\n\n"
log "Starting Tomcat \n"
sh $APACHEBIN/startup.sh
sleep 3
#log "***********************Starting Netvertex server**********************************\n"
log "\n Starting NetVertex Server \n"
cd $NVServer; sh netvertex.sh
#log "**********************************************************************************\n"
log "\nDatabase Schema is created on $DB_SERVER_IP with USRNAME : $DB_USRNAME password : $DB_USRNAME \n"
log "Netvertex user is created with USRNAME :  $USRNAME password : $USRNAME \n"
log "Please open Browser and go to link PD : http://$LOCALIP:$SM_PORT/netvertexsm\n"
log "###############################################################################################################\n\n\n\n"
log "\t\t\t##############################################################################################\n"
log "\t\t\t#       Database IP           :\t$DB_SERVER_IP \n"
log "\t\t\t##############################################################################################\n"
log "\t\t\t#       Database User         :\t$DB_USRNAME \n"
log "\t\t\t##############################################################################################\n"
log "\t\t\t#       NetVertex User        :\t$USRNAME \n"
log "\t\t\t##############################################################################################\n"
log "\t\t\t#       NetVetex Admin port   :\t$NV_ADMIN_PORT \n"
log "\t\t\t##############################################################################################\n"
log "\t\t\t#       NeVertexSM URL        :\thttp://$LOCALIP:$SM_PORT/netvertexsm \n"
log "\t\t\t##############################################################################################\n"

sed -i -e "s|\(osusrname=\).*|\1$USRNAME|" $CURRENTPATH/server.properties;
sed -i -e "s|\(osip=\).*|\1$LOCALIP|" $CURRENTPATH/server.properties;
sed -i -e "s|\(osport=\).*|\1$SM_PORT|" $CURRENTPATH/server.properties;
sed -i -e "s|\(testrunid=\).*|\1$TEST_RUN_ID|" $CURRENTPATH/server.properties;
sed -i -e "s|\(oracle=\).*|\1$ORACLE|" $CURRENTPATH/server.properties;
sed -i -e "s|\(voltdb=\).*|\1$VOLTDB|" $CURRENTPATH/server.properties;
sed -i -e "s|\(netvertex_home=\).*|\1$NV_HOME|" $CURRENTPATH/server.properties;
sed -i -e "s|\(pd_home=\).*|\1$TOMCAT_HOME/sm|" $CURRENTPATH/server.properties;
sed -i -e "s|\(java_home=\).*|\1$JAVA_HOME|" $CURRENTPATH/server.properties;

viewMyServerProcesses instalAppRunningProcess

log "Starting process with process id ${instalAppRunningProcess}"
