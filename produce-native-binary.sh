#!/bin/sh

mvn package -Pnative -Dnative-image.container-runtime=docker
#mvn package -Pnative -Dnative-image.container-runtime=podman
