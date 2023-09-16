#!/bin/bash
# jdk的bin保存的位置
JDK_HOME="/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.372.b07-1.el7_9.x86_64/jre/bin/java"
# jvm的参数
VM_OPTS="-XX:MaxPermSize=256m -XX:PermSize=128m -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseParallelGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/var/log/myapp/gc.log"
# 启动环境配置
SPB_OPTS="--spring.profiles.active=prod"
# 启动jar存放的位置
APP_LOCATION="/www/wwwroot/default/chatgpt-java-web/target/chatgpt-java-web-0.0.1-SNAPSHOT.jar"
# 将要启动进程的命令
APP_NAME="chatgpt-java-web"
# 自己的 git项目的地址
GIT_URL="https://gitee.com/lixinjiuhao/chatgpt-java-web.git"
# git clone下来 项目的路径
APP_DIR="/www/wwwroot/default/chatgpt-java-web"

initApp(){
if [ -d $APP_DIR ]; then
echo "代码已经存在，准备打包"
else
git clone $GIT_URL
echo "代码拉取成功，准备打包"
fi

# Git 拉取代码
# 进入代码目录
cd $APP_DIR
echo "进入代码根目录，准备开始打包"

#
git  pull $GIT_URL
echo "代码拉取成功！ 开始打包代码！"

# Maven 打包
mvn install -U -Dmaven.test.skip=true
echo "打包成功，开始启动项目"
}

start() {
 echo "=============================start=============================="
 PID=$(eval $PID_CMD)
 if [[ -n $PID ]]; then
    echo "$APP_NAME is already running,PID is $PID"
 else
#   /dev/null 这里的路径 自定义路径和文件输出位置
    nohup $JDK_HOME $VM_OPTS -jar $APP_LOCATION $SPB_OPTS >/www/wwwroot/default/server.log 2>\$1 &
    echo "nohup $JDK_HOME $VM_OPTS -jar $APP_LOCATION $SPB_OPTS >/www/wwwroot/default/server.log 2>\$1 &"
    PID=$(eval $PID_CMD)
    if [[ -n $PID ]]; then
       echo "Start $APP_NAME successfully,PID is $PID"
    else
       echo "Failed to start $APP_NAME !!!"
    fi
 fi
 echo "=============================start=============================="
}

stop() {
 echo "=============================stop=============================="
 PID=$(eval $PID_CMD)
 if [[ -n $PID ]]; then
    kill -15 $PID
    sleep 5
    PID=$(eval $PID_CMD)
    if [[ -n $PID ]]; then
      echo "Stop $APP_NAME failed by kill -15 $PID,begin to kill -9 $PID"
      kill -9 $PID
      sleep 2
      echo "Stop $APP_NAME successfully by kill -9 $PID"
    else
      echo "Stop $APP_NAME successfully by kill -15 $PID"
    fi
 else
    echo "$APP_NAME is not running!!!"
 fi
 echo "=============================stop=============================="
}

restart() {
  echo "=============================restart=============================="
  stop
  start
  echo "=============================restart=============================="
}

status() {
  echo "=============================status=============================="
  PID=$(eval $PID_CMD)
  if [[ -n $PID ]]; then
       echo "$APP_NAME is running,PID is $PID"
  else
       echo "$APP_NAME is not running!!!"
  fi
  echo "=============================status=============================="
}

info() {
  echo "=============================info=============================="
  echo "APP_LOCATION: $APP_LOCATION"
  echo "APP_NAME: $APP_NAME"
  echo "JDK_HOME: $JDK_HOME"
  echo "VM_OPTS: $VM_OPTS"
  echo "SPB_OPTS: $SPB_OPTS"
  echo "=============================info=============================="
}

help() {
   echo "start: start server"
   echo "stop: shutdown server"
   echo "restart: restart server"
   echo "status: display status of server"
   echo "info: display info of server"
   echo "help: help info"
}

case $1 in
start)
    initApp
    start
    ;;
stop)
    stop
    ;;
restart)
    initApp
    restart
    ;;
status)
    status
    ;;
info)
    info
    ;;
help)
    help
    ;;
*)
    help
    ;;
esac
exit $?
