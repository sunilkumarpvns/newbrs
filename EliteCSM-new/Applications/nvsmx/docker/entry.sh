#!/bin/bash

cd $PD_HOME ;


sed -i "s#url=.*#url=${url}#g" $PD_HOME/WEB-INF/database.properties
sed -i "s#username=.*#username=${username}#g" $PD_HOME/WEB-INF/database.properties
sed -i "s#password=.*#password=${password}#g" $PD_HOME/WEB-INF/database.properties
sed -i "s#maxTotal=.*#maxTotal=${maxTotal}#g" $PD_HOME/WEB-INF/database.properties
sed -i "s#maxIdle=.*#maxIdle=${maxIdle}#g" $PD_HOME/WEB-INF/database.properties
sed -i "s#validationQueryTimeout=.*#validationQueryTimeout=${validationQueryTimeout}#g" $PD_HOME/WEB-INF/database.properties
sed -i "s#testOnBorrow=.*#testOnBorrow=${testOnBorrow}#g" $PD_HOME/WEB-INF/database.properties
sed -i "s#encryptedPassword=.*#encryptedPassword=${encryptedPassword}#g" $PD_HOME/WEB-INF/database.properties

#Set vi editor to full screen
export TERM=xterm
stty rows 40 cols 150


cd $TOMCAT_HOME/bin
sh startup.sh

sleep 1m

tail -f /dev/null
