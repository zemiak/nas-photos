#!/bin/sh

XMX=$(echo $MEMORY_LIMIT | sed 's/mi//Ig' | sed 's/m//Ig')
XMX=$(expr $XMX \* 1048576 \* $MEMORY_PERCENT / 100 / 1048576)

cd ${AUTODEPLOY_FOLDER}
echo exec java -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=${QUARKUS_HTTP_PORT} -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Xms32m -Xmx${XMX}m -jar app.jar
exec java -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=${QUARKUS_HTTP_PORT} -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Xms32m -Xmx${XMX}m -jar app.jar
