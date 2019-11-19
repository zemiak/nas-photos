#!/bin/sh

mvn clean package || exit 10
docker build . -f ./src/main/docker/Dockerfile-jvm -t nasphotos
if [ $? -ne 0 ]
then
    exit 20
fi

cd ./src/main/process-movies-thumbnails || exit 30
docker build . -f Dockerfile -t process-movies-thumbnails
if [ $? -ne 0 ]
then
    exit 40
fi

cd ../../.. || exit 50
