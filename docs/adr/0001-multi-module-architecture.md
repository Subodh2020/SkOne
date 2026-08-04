# ADR 0001: Multi-Module Architecture

## Status

Accepted

## Context

SKOne is a long-lived Android developer platform. A monolithic artifact would couple
unrelated concerns (UI, AI, device APIs, networking) and force consumers to pull
unused code. We need clear module boundaries that scale for 5–10 years.

## Decision

Adopt a multi-module Gradle repository with:

- `build-logic` convention plugins for shared build configuration
- Version catalog (`gradle/libs.versions.toml`) for dependency alignment
- `skone-bom` for consumer version management
- Fine-grained published libraries under `io.skone`

Foundation modules:

| Module | Role |
|--------|------|
| `skone-common` | Shared models, logging, annotations |
| `skone-plugin` | Plugin SPI and registry |
| `skone-theme` | Design token interfaces |
| `skone-core` | SDK entry point, AI SPI, component contracts |
| `skone-bom` | Bill of Materials |

Future modules (`skone-compose`, `skone-ui`, `skone-ai-*`, device SDKs, …) are created
only when their phase begins. Coordinates are reserved in BOM documentation.

Dependency direction: `core` → `plugin` / `theme` / `common`; never reverse.

## Consequences

- Consumers depend only on needed artifacts.
- Binary compatibility can be versioned per module (aligned via BOM).
- Build complexity increases; convention plugins mitigate duplication.
- Module creation is gated by roadmap phases to avoid empty shells.
