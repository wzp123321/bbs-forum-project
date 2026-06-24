# =============================================================
#  后端服务 Dockerfile (multi-stage)
#  - builder  : 用 Maven 编译打包
#  - runtime  : 用 JRE 17 跑 jar
# =============================================================

# ===== 构建阶段 =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# 先复制 pom 拉依赖, 利用 Docker 缓存
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
RUN mvn -B -q dependency:go-offline

# 复制源码并打包 (跳过测试)
COPY src src
RUN mvn -B -q -DskipTests package

# ===== 运行阶段 =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 时区与编码
ENV TZ=Asia/Shanghai \
    LANG=zh_CN.UTF-8 \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 复制 jar
COPY --from=builder /build/target/*.jar /app/app.jar

# 上传/缓存目录
RUN mkdir -p /app/upload /app/logs

EXPOSE 8888

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8888/admin/auth/info >/dev/null 2>&1 || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
