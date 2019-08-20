#!/bin/bash

. /opt/docker-env.properties

cd /opt/server/bin/

if [ "$engine_prod" == "true" ]
then
	echo "---------This is production environments-------"
        echo "---------engine_min=${engine_min}----------------------"
        echo "---------engine_max=${engine_max}----------------------"
        echo "---------engine_maxpermsize=${engine_maxpermsize}----------------------"
        echo "---------engine_newsize=${engine_newsize}----------------------"
        echo "---------engine_maxnewsize=${engine_maxnewsize}----------------------"
	grep -rl "\-Xms128m \-Xmx256m \-Dlog4j.configuration=system/log4j.properties" ./netvertex.sh | xargs sed -i "s@-Xms128m -Xmx256m -Dlog4j.configuration=system/log4j.properties@-d64 -server ${engine_min} ${engine_max}  ${engine_maxpermsize} ${engine_newsize} ${engine_maxnewsize} -Dlog4j.configuration=system/log4j.properties@g"


else
	echo "---------This is NOT production environments------"

fi

sh netvertex.sh

tail -f /dev/null
