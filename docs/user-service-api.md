# User Service API

OpenAPI documentation for the User Service

Base servers:
- http://localhost:8081

## POST /auth/register
- Summary: register
- Request body:
  - application/json:
$ref: #/components/schemas/RegistrationRequest
- Responses:
  - 200: OK
    - */*: $ref: #/components/schemas/AuthResponse

## POST /auth/login
- Summary: login
- Request body:
  - application/json:
$ref: #/components/schemas/LoginRequest
- Responses:
  - 200: OK
    - */*: $ref: #/components/schemas/AuthResponse

## GET /auth/me
- Summary: me
- Responses:
  - 200: OK
    - */*: {'type': 'string'}

## GET /auth/hello
- Summary: hello
- Responses:
  - 200: OK
    - */*: {'type': 'object'}

## GET /admin/users
- Summary: getAllUsers
- Responses:
  - 200: OK
    - */*: array of $ref: #/components/schemas/UserDto
