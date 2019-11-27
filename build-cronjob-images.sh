#!/bin/sh

cd ./src/main/cron-moviethumbnails || exit 10
docker build . -f Dockerfile -t nasphotos-moviethumbnails
if [ $? -ne 0 ]
then
    exit 15
fi

cd ../../.. || exit 17

cd ./src/main/cron-thumbnails || exit 20
docker build . -f Dockerfile -t nasphotos-thumbnails
if [ $? -ne 0 ]
then
    exit 25
fi

cd ../../.. || exit 99
