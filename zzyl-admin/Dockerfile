# 使用更稳定的基础镜像
FROM eclipse-temurin:11-jre

# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 密钥通过运行时注入，这里只做默认声明（不要写死真实值）
ENV OSS_ACCESS_KEY_ID=""
ENV OSS_ACCESS_KEY_SECRET=""

# 拷贝jar包 (确保执行 docker build 时，jar包在当前目录下)
COPY zzyl-admin.jar /app.jar

# 暴露端口 (对应你 deploy.sh 里的 9000)
EXPOSE 9000

# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]