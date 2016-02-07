#!/bin/sh

WILDFLY=wildfly-10.0.0.Final
PROJECT=~/Documents/projects/nasphotos
DOWNLOAD=~/Downloads/$WILDFLY.zip
INSTALL=~/bin
TARGET=wildfly

cd $INSTALL
test -d $TARGET && rm -rf $TARGET
cp $DOWNLOAD ./
unzip $WILDFLY.zip >/dev/null
rm $WILDFLY.zip
mv $WILDFLY $TARGET

./$TARGET/bin/add-user.sh admin admin --silent
