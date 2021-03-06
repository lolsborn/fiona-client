#!/bin/bash -e

if [ ! $SBT_VERSION ]; then SBT_VERSION=0.7.4; fi
if [ ! $SBT_DIR ]; then SBT_DIR=$HOME/.sbt; fi
if [ ! $SBT_FILENAME ]; then SBT_FILENAME=sbt-launch-$SBT_VERSION.jar; fi
if [ ! $SBT_LOCATION ]; then SBT_LOCATION=$SBT_DIR/$SBT_FILENAME; fi

SBT_URL="http://simple-build-tool.googlecode.com/files/$SBT_FILENAME"

if [ ! -f $SBT_LOCATION ]; 
then
    echo "Could not find $SBT_FILENAME in $SBT_DIR, downloading..."
    mkdir -p $SBT_DIR
    if which wget > /dev/null; then 
        CMD="wget --progress=bar $SBT_URL -O $SBT_LOCATION"
    elif which curl > /dev/null; then
        CMD="curl $SBT_URL -o $SBT_LOCATION"
    else
        echo "Could not find wget or curl."
        exit 1
    fi
   if ! $CMD; then
      echo "Failed retrieving $SBT_URL ... cleaning up."
      if [ -f $SBT_LOCATION ]; then rm -r $SBT_LOCATION; fi
      exit 1
   fi
fi

java -jar -Xmx512m $SBT_LOCATION "$@"
