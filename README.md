Budget Management (microservices) — quick start

Minimal multi-service project with two Spring Boot services and two Postgres containers for local testing.

Prerequisites
- Docker & Docker Compose (or docker-compose)
- JDK 17+ (for running gradle locally if needed)

Quick commands (from project root)
- Rebuild project (runs tests):
  ./scripts/rebuild.sh

- Build images and start services (attached, logs in terminal):
  ./scripts/build_and_start.sh

- Stop the stack (from another terminal):
  docker compose -f compose.yaml down

Notes on behavior
- Compose uses restart: "no" so containers do not automatically restart on host reboot.
- The Dockerfile is a two-stage build: Gradle runs inside a builder image and the runtime image contains the built jar. The image build will produce the jar if none exists locally.
- The start script currently runs a local bootJar then runs the Docker build. To avoid duplicate Gradle runs either remove the local bootJar step or build images with --no-cache.

Service ports & Swagger
- User service: http://localhost:8081/hello
  Swagger UI: http://localhost:8081/swagger-ui/index.html
  OpenAPI JSON: http://localhost:8081/v3/api-docs

- Budget-room service: http://localhost:8082/hello
  Swagger UI: http://localhost:8082/swagger-ui/index.html
  OpenAPI JSON: http://localhost:8082/v3/api-docs

Database details (in docker-compose)
- user-db: Postgres 15, DB=userdb, user=user, password=userpass
- budget-db: Postgres 15, DB=budgetdb, user=budget, password=budgetpass

Troubleshooting
- If a build fails because Gradle/Java version mismatch, install JDK 17+ or use SDKMAN to set a compatible JVM.
- If Docker image build cannot find gradle wrapper files, ensure gradle/wrapper is included in the build context (.dockerignore adjusted by repo).
- If the app fails with ClassNotFoundException for org.postgresql.Driver, ensure the image/jar includes the PostgreSQL JDBC driver (this repo adds runtimeOnly 'org.postgresql:postgresql').
- To rebuild images without cache: docker compose -f compose.yaml build --no-cache

Files of interest
- compose.yaml — Docker Compose stack
- Dockerfile — two-stage build (builder + runtime)
- scripts/rebuild.sh — runs ./gradlew clean build
- scripts/build_and_start.sh — builds jar, builds images and runs docker compose up (attached)
- src/main/java/.../controller — example /hello endpoints
- src/main/java/.../config/OpenApiConfig.java — generates OpenAPI for controllers in the controller package

If you want
- I can remove the obsolete top-level version from compose.yaml, change the start script to skip the local bootJar, or add a single aggregated Swagger UI container that merges both services' OpenAPI endpoints.

