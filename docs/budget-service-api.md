# Budget Service API

OpenAPI documentation for the Budget Service

Base servers:
- http://localhost:8082

[Overview and examples](./API_DOCS.md)

## GET /rooms
- Summary: List rooms
- Responses:
  - 200: OK
    - */*: array of $ref: #/components/schemas/RoomDto

## POST /rooms
- Summary: Create a room
- Request body:
  - application/json: $ref: #/components/schemas/RoomDto
- Responses:
  - 200: OK
    - */*: $ref: #/components/schemas/RoomDto

## POST /rooms/{roomId}/users
- Summary: Add user to room
- Parameters:
  - roomId (in: path) required: True schema: {'type': 'integer', 'format': 'int64'}
- Request body:
  - application/json: $ref: #/components/schemas/RoomUserDto
- Responses:
  - 200: OK
    - */*: $ref: #/components/schemas/RoomUserDto

## GET /rooms/{roomId}/products
- Summary: List products in a room
- Parameters:
  - roomId (in: path) required: True schema: {'type': 'integer', 'format': 'int64'}
- Responses:
  - 200: OK
    - */*: array of $ref: #/components/schemas/RoomProductDto

## POST /rooms/{roomId}/products
- Summary: Add product to room
- Parameters:
  - roomId (in: path) required: True schema: {'type': 'integer', 'format': 'int64'}
- Request body:
  - application/json: $ref: #/components/schemas/RoomProductDto
- Responses:
  - 200: OK
    - */*: $ref: #/components/schemas/RoomProductDto

## GET /budgets
- Summary: test
- Responses:
  - 200: OK
    - */*: {'type': 'string'}

## POST /budgets
- Summary: createBudget
- Responses:
  - 200: OK
    - */*: {'type': 'string'}

## GET /budgets/hello
- Summary: hello
- Responses:
  - 200: OK
    - */*: {'type': 'string'}


[Back to API overview](./API_DOCS.md)
