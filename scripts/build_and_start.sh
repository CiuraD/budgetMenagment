#!/usr/bin/env bash
set -euo pipefail

# make script work regardless of current working directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

echo "Building runnable jar (bootJar)..."
"$PROJECT_ROOT/gradlew" clean bootJar

echo "Building Docker images..."
# Use docker compose (modern) or fall back to docker-compose if not available
if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
  DOCKER_COMPOSE_CMD="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  DOCKER_COMPOSE_CMD="docker-compose"
else
  echo "Error: neither 'docker compose' nor 'docker-compose' found in PATH" >&2
  exit 1
fi

$DOCKER_COMPOSE_CMD -f compose.yaml build

echo "Starting containers (foreground)..."
# start containers attached (no -d) so logs stream to terminal
$DOCKER_COMPOSE_CMD -f compose.yaml up

echo "If you need to stop the stack, use Ctrl+C or run '$DOCKER_COMPOSE_CMD -f compose.yaml down' in another terminal."
