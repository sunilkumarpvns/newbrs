#!/usr/bin/env bash
. ./docker-env.properties

if [ "$(docker ps -a | grep -w $engine_container_name)" ] ;
        then
                echo "Container already exists" ;
                docker stop $engine_container_name
                docker rm -v $engine_container_name
fi

if [ "$(docker ps -a | grep -w $engine_container_name | grep 'Dead' )" ] ;
        then
                echo "Container already exists" ;
                docker stop $engine_container_name
                docker rm -v -f $engine_container_name
fi


echo -e "\e[32mINFO:Launching Container $engine_container_name"

echo "-------------------------------------------"
printf "Container Name\t:$engine_container_name\n"
printf "Image Name\t:$engine_image_name\n\e[0m"

HOME=/opt/docker/Applications/netvertex


#docker stop engine;
#docker rm -v engine
#echo "docker run -itd --name $engine_container_name -h $engine_hostname -v $HOME/mount_point/$engine_container_name/dictionary:/opt/server/dictionary -v $HOME/mount_point/$engine_container_name/system:/opt/server/system -v $HOME/mount_point/$engine_container_name/logs:/opt/server/logs  $engine_image_name"

#docker run -itd --name $engine_container_name -h $engine_hostname --env-file ./docker-env.properties -p $engine_jmx_port:3454 -p $engine_rest_port:9000 -p $diameter_listening_port:3868 -p $radius_listening_port:2813 -v $HOME/mount_point/$engine_container_name/dictionary:/opt/server/dictionary -v $HOME/mount_point/$engine_container_name/system:/opt/server/system -v $HOME/mount_point/$engine_container_name/logs:/opt/server/logs -v $HOME/mount_point/$engine_container_name/data:/opt/server/data docker.sterlite.com:5043/netvertex-engine:14-09-2018-10 

docker run -itd --name $engine_container_name --env-file ./docker-env.properties -p $engine_jmx_port:3454 -p $diameter_listening_port:3868 -p $radius_listening_port:2813 -p $snmp_port:1161 -p $engine_rest_port:9000  -v $HOME/system:/opt/server/system:Z -v $HOME/data:/opt/server/data:Z -v $HOME/logs:/opt/server/logs:Z $engine_image_name
#docker run -itd --name $engine_container_name -h $engine_hostname --env-file ./docker-env.properties -p $engine_jmx_port:3454 -p $engine_rest_port:9000 -p $diameter_listening_port:3868 -p $radius_listening_port:2813  -v $HOME/mount_point/$engine_container_name/system:/opt/server/system -v $HOME/mount_point/$engine_container_name/logs:/opt/server/logs -v $HOME/mount_point/$engine_container_name/data:/opt/server/data docker.sterlite.com:5043/netvertex-engine:25-09-2018-123
