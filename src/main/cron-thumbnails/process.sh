#!/bin/sh

echo "Running app..."

java -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=${QUARKUS_HTTP_PORT} \
    -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
    -Xms32m -Xmx${MEMORY_LIMIT}m -jar app.jar &

echo "Waiting for deployment..."

count=10
deployed=false
while [ $count -gt 0 ]
do
    curl --fail http://localhost:${QUARKUS_HTTP_PORT} >/dev/null 2>/dev/null
    if [ $? -eq 0 ]
    then
        deployed=true
        count=0
    else
        sleep 2s
        count=$((count - 1))
    fi
done

if [ "$deployed" = "false" ]
then
    echo "The app could not be deployed!"
    exit 10
fi

curl -v http://localhost:${QUARKUS_HTTP_PORT}/backend/batch/thumbnails
