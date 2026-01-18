# =========================
# Builder stage
# =========================
FROM gradle:9.2-jdk21 AS builder
WORKDIR /home/gradle/project

# Copy entire repo (needed for multi-module build)
COPY --chown=gradle:gradle . .

# Build ONLY the user-service bootJar
RUN --mount=type=cache,target=/home/gradle/.gradle \
     ./gradlew :user-service:clean :user-service:bootJar -x test

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre-jammy AS runtime

COPY --from=builder \
  /home/gradle/project/user-service/build/libs/app.jar \
  /app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
