#!/usr/bin/env bash
#docker stop sm; docker rm -v sm
#docker run -itd --name sm --hostname=sm -p 9091:9091 --env-file /home/dopoc/sm/database.info netvertex-sm:7.2.0.1


. ./docker-env.properties

if [ "$(docker ps -a | grep -w $sm_container_name)" ] ;
        then
                echo "Container already exists" ;
                docker stop $sm_container_name
                docker rm -v $sm_container_name
fi

if [ "$(docker ps -a | grep -w $sm_container_name | grep 'Dead' )" ] ;
        then
                echo "Container already exists" ;
                docker stop $sm_container_name
                docker rm -v -f $sm_container_name
fi




echo -e "\e[32mINFO:Launching Container $sm_container_name"

echo "-------------------------------------------"
printf "Container Name\t:$sm_container_name\n"
printf "Image Name\t:$sm_image_name\n\e[0m"

HOME=/opt/docker/Applications/nvsmx

docker run -itd --name $sm_container_name --env-file docker-env.properties -p $sm_http_port:8080 -p $snmp_port:1162 -p $sm_shutdown_port:8005 -v $HOME/logs:/opt/tomcat/logs:Z -v $HOME/logs:/opt/tomcat/webapps/netvertexsm/logs:Z -v $HOME/WebContent/WEB-INF/database.properties:/opt/tomcat/webapps/netvertexsm/WEB-INF/database.properties:Z -v $HOME/WebContent/WEB-INF/NVSMXConfiguration.properties:/opt/tomcat/webapps/netvertexsm/WEB-INF/NVSMXConfiguration.properties:Z -v $HOME/WebContent/WEB-INF/keycloak.json:/opt/tomcat/webapps/netvertexsm/WEB-INF/keycloak.json:Z -v $HOME/data:/opt/tomcat/webapps/netvertexsm/data:Z $sm_image_name