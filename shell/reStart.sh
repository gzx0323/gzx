#!/usr/bin/env bashi

######################################可修改
APP_NAME=gzx-1.1.3.jar

LOG_PATH=/home/gzx/log/gzx$(date +%Y%m%d%H%M).log
#######################################

PID=$(ps -ef | grep ${APP_NAME} | grep -v grep | awk '{print $2}')

if [[ -n ${PID} ]]; then
  echo "Java process is running, PID is ${PID}. Killing..."
  kill -9 ${PID}
fi

# 启动Java进程
nohup java -jar ${APP_NAME} > ${LOG_PATH} 2>&1 &

# 检查Java进程是否启动成功
sleep 2
PID=$(ps -ef | grep ${APP_NAME} | grep -v grep | awk '{print $2}')
if [[ -n ${PID} ]]; then
  echo "Java process started successfully, PID is ${PID}."
else
  echo "Failed to start Java process."
fi
