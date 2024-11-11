# 使用Java 17作为基础镜像（你可以根据实际情况调整）
FROM openjdk:17-alpine
MAINTAINER xx8001-dean

# 设置工作目录
WORKDIR /app

# 将本地的JAR文件复制到容器内的工作目录（假设JAR文件在target目录下，根据实际情况修改）
COPY out/artifacts/demo01_jar/demo01.jar /app/demo01.jar

# 暴露应用程序运行的端口（根据配置文件中的server.port）
EXPOSE 8080

# 定义启动命令，运行Spring Boot应用程序
CMD ["java", "-jar", "demo01.jar"]