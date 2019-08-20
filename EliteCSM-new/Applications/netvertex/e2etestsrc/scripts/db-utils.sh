#!/bin/bash

source ./log-utils.sh
source ./utils.sh
source ./server-utils.sh

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
