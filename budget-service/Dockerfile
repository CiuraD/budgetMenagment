FROM gradle:9.2-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew :budget-service:clean :budget-service:bootJar -x test

FROM eclipse-temurin:21-jre-jammy
COPY --from=builder \
  /home/gradle/project/budget-service/build/libs/app.jar \
  /app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
