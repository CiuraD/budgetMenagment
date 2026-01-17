# Use a two-stage build: builder runs Gradle (using the wrapper) and produces the fat jar,
# runtime stage copies the produced jar into a slim JRE image.

FROM gradle:9.2-jdk21 AS builder
WORKDIR /home/gradle/project
# copy project files and build the fat jar inside the builder
COPY --chown=gradle:gradle . .
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre-jammy AS runtime
# copy the built jar from the builder stage (use glob to pick the produced jar)
COPY --from=builder /home/gradle/project/build/libs/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
