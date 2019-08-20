from tomcat:8

COPY build/exploded /opt/tomcat/webapps/netvertexsm

COPY docker/entry.sh /opt

ENV PD_HOME=/opt/tomcat/webapps/netvertexsm

WORKDIR /opt

ENTRYPOINT ["/bin/bash", "entry.sh" ]

