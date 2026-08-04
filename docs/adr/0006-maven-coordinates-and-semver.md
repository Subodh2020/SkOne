# ADR 0006: Maven Coordinates and Semantic Versioning

## Status

Accepted

## Context

SKOne will be published to Maven Central for open-source and enterprise consumers.
Coordinates and versioning must remain stable and predictable.

## Decision

| Item | Value |
|------|-------|
| Group ID | `com.thesubodhgupta.skone` (verified Maven Central namespace) |
| Package root | `io.skone.*` |
| Versioning | Semantic Versioning 2.0.0 (+ optional `-alpha`/`-beta`/`-rc`/`-SNAPSHOT`) |
| Alignment | `com.thesubodhgupta.skone:skone-bom` |
| Source of truth | `GROUP` / `VERSION_NAME` in `gradle.properties` |
| Convention | `skone.publish` |

Artifact IDs match module names (`skone-core`, `skone-common`, …).

See [ADR 0013](0013-maven-central-publishing.md) and [PUBLISHING.md](../PUBLISHING.md).

## Consequences

- Consumers pin one BOM version for aligned libraries.
- Renaming Maven group after first public Central release is a breaking change.
- Kotlin packages stay on `io.skone.*` for API stability.
