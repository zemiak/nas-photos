#!/bin/sh

cd ./src/main/cron-moviethumbnails || exit 10
docker build . -f Dockerfile -t nasphotos-moviethumbnails
if [ $? -ne 0 ]
then
    exit 15
fi

cd ../../.. || exit 17
