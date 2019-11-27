#!/bin/bash

DOCKER_MEMORY=72m
XMX=64

docker run -ti --rm -m ${DOCKER_MEMORY} -v /Volumes/media/Pictures:/data \
    -e MEMORY_LIMIT=${XMX} -e PHOTOPATH=/data nasphotos-thumbnails
