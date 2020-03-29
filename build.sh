#!/bin/sh

mvn clean package || exit 10
docker build . -f ./src/main/docker/Dockerfile-rpi -t nasphotos
if [ $? -ne 0 ]
then
    exit 20
fi
