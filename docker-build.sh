#!/usr/bin/env bash
# 登录用户
user=${1}
# 登录密码
pwd=${2}

# 相关服务name，获取当前项目名称目录
dir=$(basename `pwd`)
# 获取当前分支提交的commitID
date_=$(date "+%m_%d_%H_%M")
project=$(basename `pwd`)
branch=$(git name-rev --name-only HEAD)
commit=$(git rev-parse --short HEAD)
tag=${date_}"-"${commit}
# 获取当前分支名称
branch=`git rev-parse --abbrev-ref HEAD`
# 定义 镜像名称
IMAGE_NAME=harbor-g42c.corp.matrx.team/crystal-test/${project}/${branch}:${tag}
echo "准备构建....."
#docker login --username=ci --password=Ci123456 192.168.203.51

cd `dirname $0`




echo "删除之前版本..."
docker rmi $(docker images  | grep ${dir}| awk '{print $3}')
echo "开始构建应用程序..."
mvn -s /Users/zsx/.m2/settings.xml clean package -DskipTests -U

dstate=$?

case ${dstate} in
    "0")
         echo "开始构建docker镜像"
         docker build -f Dockerfile -t ${IMAGE_NAME} .
         echo  "${IMAGE_NAME} 构建成功，开始上传至远程仓库"
         docker  push ${IMAGE_NAME}
        ;;

    *)
      echo "编译失败！无法构建镜像！"
      echo "Compile failed! Unable to build image!"
      exit 1
       ;;
esac
