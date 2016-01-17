#!/bin/sh

WILDFLY=wildfly-10.0.0.CR3
PROJECT=~/Documents/projects/movies
DOWNLOAD=~/Downloads/$WILDFLY.zip
INSTALL=~/bin
TARGET=wildfly

cd $INSTALL
test -d $TARGET && rm -rf $TARGET
cp $DOWNLOAD ./
unzip $WILDFLY.zip >/dev/null
rm $WILDFLY.zip
mv $WILDFLY $TARGET
cd $TARGET/standalone/configuration
rm standalone*.xml
cp $PROJECT/src/main/setup/standalone-dev.xml ./standalone.xml
ln -s standalone.xml standalone-full.xml
ln -s standalone.xml standalone-full-ha.xml
ln -s standalone.xml standalone-ha.xml
cd ../../..

# Restore the database
mkdir -p /tmp/movies-restore
rm -f /tmp/movies-restore/*
cp $PROJECT/src/dev/resources/movies.plain.bz2 /tmp/movies-restore/
bunzip2 /tmp/movies-restore/movies.plain.bz2
rm -rf /tmp/movies.*
java \
    -cp ./$TARGET/modules/system/layers/base/com/h2database/h2/main/h2-1.3.173.jar \
    org.h2.tools.RunScript -url jdbc:h2:/tmp/movies -user sa -script /tmp/movies-restore/movies.plain

./$TARGET/bin/add-user.sh admin admin --silent
