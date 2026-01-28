# API Documentation Index

This folder contains API references and generated OpenAPI artifacts for the services in this repository.

Available documents

- API overview and examples: ./API_DOCS.md
- User service reference (OpenAPI-derived): ./user-service-api.md
- Budget service reference (OpenAPI-derived): ./budget-service-api.md
- Raw OpenAPI JSON (if generated): ./user-service-openapi.json, ./budget-service-openapi.json

Regenerating docs

1. Start the services locally (user-service on :8081, budget-service on :8082) or adjust OPENAPI_SERVICES env var.
2. Run: python3 scripts/generate_api_docs.py
3. The script will update the files above under docs/.

If you want automated publishing to GitHub Pages, I can add a CI workflow to build and publish docs/ on push.

