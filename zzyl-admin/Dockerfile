# 基础镜像
FROM openjdk:11.0-jre-buster
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV OSS_ACCESS_KEY_ID LTAI5tFww3VE7EZSs9dfh7j5
ENV OSS_ACCESS_KEY_SECRET 3eVYBbuK1F8rX5Tv0ge8lkKG1TrOLs
# 拷贝jar包
COPY zzyl-admin.jar /app.jar
# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]