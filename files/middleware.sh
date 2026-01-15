#!/bin/bash

set +e
rm -rf /root/data/mysql
docker rm -fv mysql
set -e

docker run -d --name mysql \
  --restart=always \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -v /root/data/mysql:/var/lib/mysql \
  mysql:8.0.43

set +e
rm -rf /root/data/nacos
docker rm -fv nacos
set -e

# nacos启动前需先手动建库建表，以下链接包含相关SQL（仅建表语句，需手动补充建库语句）
# https://github.com/alibaba/nacos/blob/3.1.0/distribution/conf/mysql-schema.sql
# NACOS_AUTH_ENABLE=true，若为false则nacos的网页控制面板不需要密码即可登录，微服务连接nacos也不需要用户名密码
# 若设置了NACOS_AUTH_ENABLE=true，则以下变量必须设置
# NACOS_AUTH_IDENTITY_KEY、NACOS_AUTH_IDENTITY_VALUE，用途未知（不是nacos的登录用户名和密码），变量值可任意自定义
# 不设置会导致nacos控制面板在打开时报告403
# NACOS_AUTH_TOKEN，用途未知，默认值如命令所示，格式为SecretKey后接base64字符串
docker run -d --name nacos \
  --restart=always \
  -p 8848:8848 -p 8849:8080 -p 9848:9848 -p 9849:9849 \
  -e MODE=standalone \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=vm.honoka.de \
  -e MYSQL_SERVICE_USER=root \
  -e MYSQL_SERVICE_PASSWORD=root123 \
  -e MYSQL_SERVICE_DB_NAME=nacos \
  -e NACOS_AUTH_ENABLE=true \
  -e NACOS_AUTH_IDENTITY_KEY=nacos \
  -e NACOS_AUTH_IDENTITY_VALUE=nacos \
  -e NACOS_AUTH_TOKEN=SecretKey012345678901234567890123456789012345678901234567890123456789 \
  -v /root/data/nacos/logs:/home/nacos/logs \
  nacos/nacos-server:v3.1.0
