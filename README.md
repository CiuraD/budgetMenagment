# Budget Management (microservices)

Minimal multi-service project with two Spring Boot services and two Postgres containers for local testing.

## Quick start

### Prerequisites
- Docker & Docker Compose (or docker-compose)
- JDK 17+ (for running gradle locally if needed)

### Quick commands (from project root)
- Rebuild project (runs tests):
  ./scripts/rebuild.sh

- Build images and start services (attached, logs in terminal):
  ./scripts/build_and_start.sh

- Stop the stack (from another terminal):
  docker compose -f compose.yaml down

### Notes on behavior
- Compose uses restart: "no" so containers do not automatically restart on host reboot.
- The Dockerfile is a two-stage build: Gradle runs inside a builder image and the runtime image contains the built jar. The image build will produce the jar if none exists locally.
- The start script currently runs a local bootJar then runs the Docker build. To avoid duplicate Gradle runs either remove the local bootJar step or build images with --no-cache.

### Service ports & Swagger
- User service: http://localhost:8081/hello
  Swagger UI: http://localhost:8081/swagger-ui/index.html
  OpenAPI JSON: http://localhost:8081/v3/api-docs

- Budget-room service: http://localhost:8082/hello
  Swagger UI: http://localhost:8082/swagger-ui/index.html
  OpenAPI JSON: http://localhost:8082/v3/api-docs

### Database details (in docker-compose)
- user-db: Postgres 15, DB=userdb, user=user, password=userpass
- budget-db: Postgres 15, DB=budgetdb, user=budget, password=budgetpass

### Troubleshooting
- If a build fails because Gradle/Java version mismatch, install JDK 17+ or use SDKMAN to set a compatible JVM.
- If Docker image build cannot find gradle wrapper files, ensure gradle/wrapper is included in the build context (.dockerignore adjusted by repo).
- If the app fails with ClassNotFoundException for org.postgresql.Driver, ensure the image/jar includes the PostgreSQL JDBC driver (this repo adds runtimeOnly 'org.postgresql:postgresql').
- To rebuild images without cache: docker compose -f compose.yaml build --no-cache

### Files of interest
- compose.yaml — Docker Compose stack
- Dockerfile — two-stage build (builder + runtime)
- scripts/rebuild.sh — runs ./gradlew clean build
- scripts/build_and_start.sh — builds jar, builds images and runs docker compose up (attached)
- src/main/java/.../controller — example /hello endpoints
- src/main/java/.../config/OpenApiConfig.java — generates OpenAPI for controllers in the controller package
- docs/ — repository-hosted API artifacts and reference
  - docs/index.md — docs landing page with links to API references
  - docs/API_DOCS.md — REST API overview and examples
  - docs/user-service-api.md — user-service OpenAPI-derived reference
  - docs/budget-service-api.md — budget-service OpenAPI-derived reference

## Business documentation

### Purpose & scope
- Purpose: provide a lightweight microservice reference implementation for managing users and budget/room-related domain objects used for local development, demos, and integration testing.
- Scope: this repository targets local development and evaluation. It includes two independent services (user-service, budget-service) each with its own database and HTTP API.

### Business goals & success criteria
- Enable teams to iterate on user and budget features independently without shared schema changes.
- Provide reproducible local environments for development and QA using Docker Compose.
- Clear REST APIs with OpenAPI/Swagger to speed integration with front-end or other services.
- Success metrics: short local setup time (<10 min), reproducible test runs, clear API contracts.

### Primary stakeholders
- Product owners: define features and acceptance criteria for user and budget capabilities.
- Developers: extend services, write tests, and integrate with frontends or other backends.
- QA / Test engineers: run integration tests and validate behaviors in ephemeral environments.
- DevOps: build images, run stacks in CI, and operate deployments.

### Core business processes
- User lifecycle: create, read, update, delete user records; basic hello endpoint demonstrates connectivity.
- Budget/Room management: create and manage budget entities and rooms (domain-specific actions are implemented in budget-service controllers and services).
- Integration & testing: services expose OpenAPI docs for easy contract testing and mocking.

### Key entities & data flows (summary)
- User: identity and contact information stored in user-db. Primarily accessed by user-service.
- Budget: financial or allocation object stored in budget-db and managed by budget-service.
- Room (domain-specific): an example domain entity related to budgets, stored in budget-db.
- Data flow: clients invoke REST APIs -> services perform business logic -> services persist to their own Postgres databases. Services are isolated and do not share DBs directly.

### Integration points
- REST APIs (OpenAPI) between services and clients. Use Swagger UI to explore and validate endpoints.
- Serwisy powinny komunikować się po REST API, które powinno być udokumentowane w dostępnym miejscu (np. README, GitHub pages, wiki).
- Services must communicate over REST APIs and the APIs must be documented in an accessible location (e.g. README, GitHub Pages, wiki).
- The generated API documentation is available in docs/index.md and docs/API_DOCS.md. Per-service references are in docs/user-service-api.md and docs/budget-service-api.md. Regenerate with scripts/generate_api_docs.py when services are running locally.
- Databases: each service owns its database (user-db, budget-db). Backups, migrations and schema changes should be coordinated per-service.
- Possible external integrations: authentication provider (OIDC), analytics/reporting, billing systems.

### Non-functional requirements & recommendations
- Scalability: services are stateless; scale horizontally by running multiple replicas behind a load balancer.
- Availability: run databases with replication and use health checks for service orchestration.
- Security: do not commit secrets. Move DB passwords and sensitive config to environment variables or a secrets manager. Use TLS for inter-service and external traffic in production.
- Observability: add structured logging, metrics (Prometheus), and distributed tracing (Jaeger) for production-grade observability.
- Data retention & privacy: implement retention policies and deletion flows to comply with privacy regulations (GDPR) where applicable.

### Deployment & operations
- Local: use scripts/build_and_start.sh and compose.yaml for quick local stacks.
- CI/CD: build fat jars or Docker images in pipeline, run unit and integration tests, push images to registry, and deploy using Helm/Kubernetes or Docker Compose in higher environments.
- Backups: schedule DB dumps and test restore procedures regularly.
- Monitoring & alerts: collect logs/metrics and configure alerts on error rates, latency, and resource exhaustion.

### Security & compliance
- Secrets management: use vault or cloud provider secret stores for production credentials.
- Access control: add authentication and authorization to APIs; restrict administrative actions.
- Audit & logging: retain audit logs for sensitive operations and ensure logs are tamper-evident if required.
- Regulatory: consider encryption at rest, data minimization, and consent management for user data.

### Roadmap & recommended next steps
- Add authentication/authorization (JWT/OAuth2) and secure endpoints.
- Add CI pipelines for Docker image builds and automated deployment to staging.
- Add database migration tooling (Flyway or Liquibase) and make schema changes backward compatible.
- Improve observability: add metrics, tracing, and centralized logging.
- Add documentation pages per-service and API reference auto-generated from OpenAPI.

### Contributing & contact
- Contributions: fork, make changes, and open pull requests. Follow existing code style and include tests for new behavior.
- For questions or to become a maintainer, contact the current project owners listed in the repository metadata or use the repository issue tracker.
