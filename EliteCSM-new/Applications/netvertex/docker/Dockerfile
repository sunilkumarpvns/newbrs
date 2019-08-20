
from oracle-jdk:1.8

COPY build/install/netvertex* /opt/server

COPY docker/entry.sh docker/docker-env.properties /opt/

RUN mkdir -p /opt/server/logs

ENV NETVERTEX_HOME=/opt/server

WORKDIR /opt

ENTRYPOINT ["/bin/bash", "entry.sh" ]