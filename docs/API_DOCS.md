# REST API Documentation

This document lists all REST endpoints implemented in the repository (user-service and budget-service), with request/response schemas, validation notes and example curl commands.

Service base URLs (local default)
- user-service: http://localhost:8081
- budget-room service: http://localhost:8082

Note: services must communicate over REST APIs and APIs must be documented in an accessible location (README, GitHub Pages, wiki).

---

USER SERVICE (auth)
Base path: /auth

1) GET /auth/hello
- Description: basic connectivity check
- Request: none
- Response: 200 OK
  - Body: plain text string, e.g. "hello from user-service"
- Example:
  curl -s http://localhost:8081/auth/hello

2) POST /auth/register
- Description: register a new user
- Request: 201 Created on success, 409 Conflict if username already exists
- Request body (application/json): RegistrationRequest
  - username (string, required)
  - password (string, required)
  - email (string, required, email format)
  - nickname (string, required)
  - roles (array of role names, optional)
- Response body (application/json): AuthResponse (201)
  - id (number)
  - username (string)
  - email (string)
  - nickname (string)
  - roles (array of role names)
  - token (string) — authentication token issued on registration
- Example:
  curl -X POST http://localhost:8081/auth/register \
    -H 'Content-Type: application/json' \
    -d '{"username":"alice","password":"s3cret","email":"a@ex.com","nickname":"Alice"}'

3) POST /auth/login
- Description: authenticate existing user
- Request: LoginRequest (application/json)
  - username (string, required)
  - password (string, required)
- Response:
  - 200 OK with AuthResponse (same shape as register response, includes token)
  - 401 Unauthorized if credentials invalid
- Example:
  curl -X POST http://localhost:8081/auth/login \
    -H 'Content-Type: application/json' \
    -d '{"username":"alice","password":"s3cret"}'

4) GET /auth/me
- Description: returns a simple authenticated response; used to verify authentication/authorization
- Request: Authorization header typically required in production, returns 200 OK with text
- Example:
  curl -H "Authorization: Bearer <token>" http://localhost:8081/auth/me

AuthResponse schema (returned by register/login)
- id: number
- username: string
- email: string
- nickname: string
- roles: array of role strings
- token: string (JWT or token string, if authentication implemented)

---

BUDGET SERVICE (rooms, products, budgets)
Base paths: /rooms, /budgets

ROOMS API

1) POST /rooms
- Description: create a new room
- Request body (application/json): RoomDto
  - roomId: number (ignored on create; assigned by server)
  - roomName: string (required, not blank)
- Response:
  - 201 Created
  - Location header: /rooms/{roomId}
  - Body: created RoomDto (with assigned roomId)
- Example:
  curl -X POST http://localhost:8082/rooms \
    -H 'Content-Type: application/json' \
    -d '{"roomName":"Weekend groceries"}'

2) GET /rooms
- Description: list all rooms
- Response: 200 OK
  - Body: array of RoomDto
- Example:
  curl http://localhost:8082/rooms

3) POST /rooms/{roomId}/users
- Description: add a user to a room (membership)
- Path parameters:
  - roomId (number)
- Request body (application/json): RoomUserDto
  - userId: number (required)
  - roomId: number (optional, server uses path param)
  - isAdmin: boolean (optional)
- Response: 200 OK
  - Body: RoomUserDto representing the membership
- Validation: userId is @NotNull
- Example:
  curl -X POST http://localhost:8082/rooms/1/users \
    -H 'Content-Type: application/json' \
    -d '{"userId":42, "isAdmin": false}'

4) POST /rooms/{roomId}/products
- Description: add a product/expense to a room
- Path parameters:
  - roomId (number)
- Request body (application/json): RoomProductDto
  - productId: number (ignored on create)
  - roomId: number (optional)
  - productName: string (required, not blank)
  - price: number (required, positive or zero)
  - isPaid: boolean
- Response:
  - 201 Created
  - Location header: /rooms/{roomId}/products/{productId}
  - Body: created RoomProductDto (with assigned productId)
- Example:
  curl -X POST http://localhost:8082/rooms/1/products \
    -H 'Content-Type: application/json' \
    -d '{"productName":"Milk","price":2.50,"isPaid":false}'

5) GET /rooms/{roomId}/products
- Description: list products for a room
- Path parameters:
  - roomId (number)
- Response: 200 OK
  - Body: array of RoomProductDto
- Example:
  curl http://localhost:8082/rooms/1/products

Room DTOs summary
- RoomDto:
  - roomId: number
  - roomName: string (not blank)
- RoomUserDto:
  - userId: number (required)
  - roomId: number
  - isAdmin: boolean
- RoomProductDto:
  - productId: number
  - roomId: number
  - productName: string (required, not blank)
  - price: decimal (required, >= 0)
  - isPaid: boolean

BUDGETS API

1) GET /budgets/hello
- Description: health/hello endpoint for budgets API
- Response: 200 OK with plain text
- Example:
  curl http://localhost:8082/budgets/hello

2) GET /budgets
- Description: example endpoint that returns greeting including current user id (from security context)
- Response: 200 OK with plain text
- Example:
  curl -H "Authorization: Bearer <token>" http://localhost:8082/budgets

3) POST /budgets
- Description: create a budget (example secured endpoint). In code annotated with @PreAuthorize("hasRole('USER')").
- Response: 200 OK with plain text (example implementation returns "Budget created")
- Example:
  curl -X POST -H "Authorization: Bearer <token>" http://localhost:8082/budgets

Security notes
- Some endpoints assume authentication/authorization is present (e.g. /budgets POST). In local/demo profiles these may be no-ops; production must secure endpoints (JWT/OAuth2).
- Include Authorization: Bearer <token> header where appropriate.

Publishing & maintenance
- Generated OpenAPI JSON is available at /v3/api-docs when services are running. Use Swagger UI (paths shown in README) to view interactive docs.
- Conventions:
  - Keep OpenAPI accurate and commit generated artifacts or publish via CI to GitHub Pages.
  - Update docs/ and README links when adding/changing endpoints.

If you want, I can automatically fetch /v3/api-docs from running services and generate formatted markdown per endpoint and add it to docs/. Request me to generate and publish these artifacts.
