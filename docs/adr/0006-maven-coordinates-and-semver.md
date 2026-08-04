# ADR 0006: Maven Coordinates and Semantic Versioning

## Status

Accepted

## Context

SKOne will be published to Maven Central for open-source and enterprise consumers.
Coordinates and versioning must remain stable and predictable.

## Decision

| Item | Value |
|------|-------|
| Group ID | `io.skone` |
| Package root | `io.skone.*` |
| Versioning | Semantic Versioning 2.0.0 |
| Alignment | `io.skone:skone-bom` |

Artifact IDs match module names (`skone-core`, `skone-common`, …).

Reserved artifact IDs (created in later phases):

`skone-compose`, `skone-xml`, `skone-ui`, `skone-forms`, `skone-navigation`,
`skone-feedback`, `skone-validation`, `skone-animation`, `skone-ai-core`,
`skone-ai-ui`, `skone-ai-chat`, `skone-ai-voice`, `skone-ai-image`,
`skone-camera`, `skone-location`, `skone-map`, `skone-network`, `skone-socket`,
`skone-auth`, `skone-storage`.

License placeholder for publish skeleton: Apache License 2.0.

## Consequences

- Consumers pin one BOM version for aligned libraries.
- Individual modules may gain independent versioning later; BOM remains the
  recommended entry point.
- Renaming artifacts after first public release is a breaking change and avoided.
