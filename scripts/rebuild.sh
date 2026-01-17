#!/usr/bin/env bash
set -euo pipefail

# make script work regardless of current working directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

echo "Rebuilding Gradle project..."
"$PROJECT_ROOT/gradlew" clean build

echo "Build finished."
