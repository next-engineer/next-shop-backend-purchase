# ── Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle wrapper / 설정 먼저 복사(캐시 최적화)
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./
RUN sed -i 's/\r$//' gradlew && chmod +x ./gradlew

# (선택) 의존성 미리 받아 캐시 고정 — 실패하면 이 줄은 지워도 됨
RUN ./gradlew --no-daemon dependencies || true

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

# ── Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# 기본 런타임 값들 (ECS에서 원하는 값으로 덮어쓰기 가능)
ENV SPRING_PROFILES_ACTIVE=container \
    SERVER_PORT=8082 \
    TZ=Asia/Seoul \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8082

# exec form 사용: 신호(SIGTERM 등)가 JVM까지 정확히 전달되도록
ENTRYPOINT ["/bin/sh","-c","exec java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Dserver.port=${SERVER_PORT} -jar /app/app.jar"]
