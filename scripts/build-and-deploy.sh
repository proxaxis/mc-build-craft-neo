#!/usr/bin/env bash
# Build buildcraftneo in the app container and deploy the jar to the testserver.
# Usage: scripts/build-and-deploy.sh [--restart]
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APP=mc_build_craft_neo_app
SRV=mc_build_craft_neo_testserver

docker exec \
  -e JAVA_HOME=/home/developper/tools/jdk-25 \
  -e PATH=/home/developper/tools/jdk-25/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
  "$APP" bash -c 'cd /app && ./gradlew build --no-daemon -x javadoc'

JAR=$(ls -t "$ROOT"/app/build/libs/buildcraftneo-*-*.jar "$ROOT"/app/build/libs/buildcraftneo-*.jar 2>/dev/null | grep -v sources | head -1)
echo "Deploying $JAR"
docker exec "$SRV" rm -f '/data/mods/buildcraftneo-'*.jar || true
docker cp "$JAR" "$SRV":/data/mods/
if [[ "${1:-}" == "--restart" ]]; then
  docker restart "$SRV"
fi
