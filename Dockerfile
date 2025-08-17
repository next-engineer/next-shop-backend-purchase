FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./
RUN sed -i 's/\r$//' gradlew && chmod +x ./gradlew
COPY src ./src
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SERVER_PORT=8082
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["/bin/sh","-c","java $JAVA_OPTS -Dserver.port=${SERVER_PORT} -jar /app/app.jar"]
