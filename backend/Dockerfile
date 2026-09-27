# syntax=docker/dockerfile:1
# Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    sh -c 'for attempt in 1 2 3; do mvn -B -q -Dmaven.wagon.http.retryHandler.count=5 dependency:go-offline && exit 0; sleep $((attempt * 5)); done; exit 1'
COPY src src
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mvn -B -Dmaven.wagon.http.retryHandler.count=5 clean package
FROM eclipse-temurin:21-jre
LABEL org.opencontainers.image.vendor="上海如静知华信息科技有限公司" org.opencontainers.image.source="https://www.zhuatech.cn/"
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* && useradd --system --uid 10001 zhuatech
WORKDIR /app
COPY --from=build /workspace/target/zhuatech-crm.jar app.jar
USER zhuatech
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
