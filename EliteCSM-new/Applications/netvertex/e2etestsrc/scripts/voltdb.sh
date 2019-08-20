#!/usr/bin/env bash
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

killVoltDB() {

find ${WORKSPACE}/installation -name "server.properties" | xargs grep voltdbport | awk -F "=" '{print $NF}' | uniq > ${WORKSPACE}/installation/voltdbportstokill.txt

for i in `cat ${WORKSPACE}/installation/voltdbportstokill.txt`
do
	VOLTPROCESSTOKILL=`netstat -anp | grep -w $i | awk '{print $NF}' | awk -F "/" '{print $1}'`
	if [[ ${VOLTPROCESSTOKILL} -gt 0 ]]
	then
		kill -9 ${VOLTPROCESSTOKILL}
	fi

#	netstat -ap | grep -w $i | awk '{print $NF}' | awk -F "/" '{print $1}' > ${WORKSPACE}/installation/voltdbprocesstokill.txt
done


}
