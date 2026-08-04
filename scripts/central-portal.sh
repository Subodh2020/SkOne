#!/usr/bin/env bash
# Maven Central Portal helpers for SKOne.
# Uses the Publisher API (https://central.sonatype.com/api/v1/publisher) — not OSSRH.
set -euo pipefail

API_BASE="${CENTRAL_PORTAL_API:-https://central.sonatype.com/api/v1/publisher}"
GROUP_PATH="com/thesubodhgupta/skone"
MAVEN_CENTRAL_BASE="https://repo.maven.apache.org/maven2/${GROUP_PATH}"
PORTAL_UI="https://central.sonatype.com/"

auth_header() {
  if [[ -z "${CENTRAL_PORTAL_USERNAME:-}" || -z "${CENTRAL_PORTAL_PASSWORD:-}" ]]; then
    echo "✗ CENTRAL_PORTAL_USERNAME / CENTRAL_PORTAL_PASSWORD required" >&2
    exit 1
  fi
  local token
  token="$(printf '%s:%s' "$CENTRAL_PORTAL_USERNAME" "$CENTRAL_PORTAL_PASSWORD" | base64 | tr -d '\n')"
  echo "Authorization: Bearer ${token}"
}

extract_state() {
  python3 -c 'import json,sys; d=json.load(sys.stdin); print(d.get("deploymentState") or d.get("state") or "UNKNOWN")'
}

cmd_status() {
  local id="${1:?deployment id required}"
  # Portal Publisher API: POST /status?id=…
  curl -sS -X POST -H "$(auth_header)" "${API_BASE}/status?id=${id}"
}

cmd_wait() {
  local id="${1:?deployment id required}"
  local want="${2:?desired state required}" # VALIDATED | PUBLISHED
  local max_seconds="${3:-900}"
  local interval=10
  local elapsed=0
  echo "Waiting for deployment ${id} → ${want} (timeout ${max_seconds}s, poll ${interval}s)"
  while (( elapsed <= max_seconds )); do
    local body state
    body="$(cmd_status "$id")"
    state="$(printf '%s' "$body" | extract_state || echo PARSE_ERROR)"
    echo "Deployment State: ${state}"
    if [[ "$state" == "$want" ]]; then
      echo "✓ Deployment reached ${want}"
      printf '%s\n' "$body"
      return 0
    fi
    if [[ "$state" == "FAILED" || "$state" == "FAILED_VALIDATION" ]]; then
      echo "✗ Deployment failed:"
      printf '%s\n' "$body"
      exit 1
    fi
    sleep "$interval"
    elapsed=$((elapsed + interval))
  done
  echo "✗ Timed out waiting for ${want} after ${max_seconds}s"
  cmd_status "$id" || true
  exit 1
}

cmd_publish() {
  local id="${1:?deployment id required}"
  echo "Publishing deployment ${id}…"
  local code
  code="$(curl -sS -o /tmp/skone-central-publish.out -w '%{http_code}' \
    -X POST -H "$(auth_header)" "${API_BASE}/deployment/${id}")"
  cat /tmp/skone-central-publish.out || true
  echo
  if [[ "$code" != "20"* && "$code" != "204" && "$code" != "200" && "$code" != "201" && "$code" != "202" ]]; then
    echo "✗ Publish request failed (HTTP ${code})"
    exit 1
  fi
  echo "✓ Publish requested for ${id} (HTTP ${code})"
}

cmd_verify_maven() {
  local version="${1:?version required}"
  local artifact="${2:-skone-bom}"
  local url="${MAVEN_CENTRAL_BASE}/${artifact}/${version}/"
  echo "Checking Maven Central: ${url}"
  local code
  code="$(curl -sS -o /dev/null -w '%{http_code}' "$url" || true)"
  if [[ "$code" == "200" ]]; then
    echo "✓ Artifact listing available"
    echo "Artifact URL: ${url}"
    return 0
  fi
  echo "⚠ Artifact not yet visible on Maven Central (HTTP ${code})."
  echo "  Portal UI: ${PORTAL_UI}"
  echo "  Repo root: ${MAVEN_CENTRAL_BASE}/"
  echo "  Propagation can take minutes after PUBLISHED."
  return 0
}

usage() {
  cat <<EOF
Usage:
  $0 status <deploymentId>
  $0 wait <deploymentId> <VALIDATED|PUBLISHED> [timeoutSeconds]
  $0 publish <deploymentId>
  $0 verify-maven <version> [artifactId]
EOF
}

main() {
  local cmd="${1:-}"
  shift || true
  case "$cmd" in
    status) cmd_status "$@" ;;
    wait) cmd_wait "$@" ;;
    publish) cmd_publish "$@" ;;
    verify-maven) cmd_verify_maven "$@" ;;
    *) usage; exit 1 ;;
  esac
}

main "$@"
