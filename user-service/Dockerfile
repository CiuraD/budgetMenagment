FROM gradle:9.2-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,id=user-service-gradle-cache,target=/home/gradle/.gradle \
    ./gradlew :user-service:clean :user-service:bootJar -x test

FROM eclipse-temurin:21-jre-jammy
COPY --from=builder \
  /home/gradle/project/user-service/build/libs/app.jar \
  /app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]