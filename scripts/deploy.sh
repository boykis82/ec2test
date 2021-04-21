#!/bin/bash

REPOSITORY=/home/ubuntu/app/step2
PROJECT_NAME=miniswing-pilot

echo "> file copy"
cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> pid 확인"
CURRENT_PID=$(pgrep -fl miniswing-pilot | grep jar | awk '{print $1}')

echo "pid = $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "..."
else
        echo "> kill -15 $CURRENT_PID"
        kill -15 $CURRENT_PID
        sleep 5
fi

echo " > new deploy"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo " > jar name: $JAR_NAME"

chmod +x $JAR_NAME

echo " > jar execution"

nohup java -jar $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &