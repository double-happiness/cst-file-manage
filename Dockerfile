#FROM harbor-g42c.corp.matrx.team/matrx-docker/base-server/centos-jmx8-sctp:v2.7.5
FROM harbor-g42c.corp.matrx.team/baselibrary/openjdk:21-jdk-slim
MAINTAINER benben
USER 0
#ARG 来自pom文件中buildargs的配置

ARG jarFile
ENV JVM_OPTS -server -XX:MetaspaceSize=64m -XX:MaxGCPauseMillis=50
ADD ./target/moms-mtmgr-0.0.1-SNAPSHOT.jar  mtmgr.jar
ENV TZ=Asia/Dubai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN chmod +x /var/log   #日志文件目录
ENTRYPOINT  java ${JVM_OPTS} ${ENV_MEM_JVM_OPTS} -Djava.security.egd=filey:/dev/./urandom -Dfile.encoding=UTF-8 -Duser.timezone=GMT+04 -jar mtmgr.jar
