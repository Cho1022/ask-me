# Repository Working Guide

## Project

- `frontend/`: Vue 3, TypeScript, Vite, Pinia
- `backend/`: Java 21, Spring Boot, JPA, Flyway
- `docker-compose.yml`: Vue, Spring Boot, MySQL local stack

## Rules

- Read the affected flow before editing.
- Keep Vue components focused and shared state in Pinia only when multiple components use it.
- Do not expose JPA entities directly from controllers.
- Recalculate order prices on the server.
- Change schema and seed data only through Flyway migrations.
- Never commit Google credentials, `.env`, build output, or dependencies.
- Preserve the separation between transcription, text parsing, and order confirmation.

## Verification

- Frontend: `npm run test:run`, then `npm run build` in `frontend/`.
- Backend on Windows: run `.\scripts\test-backend.ps1` at the repository root, then `gradlew.bat build -x test` in `backend/`.
- Backend on Linux/CI: run `./gradlew test build --no-daemon` in `backend/`.
- Backend integration tests require Docker Desktop for Testcontainers MySQL.
- Run `docker compose config` after changing Compose files.
