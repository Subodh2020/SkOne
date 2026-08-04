# ADR 0002: Plugin Architecture

## Status

Accepted

## Context

SKOne must support Camera, Location, AI, Analytics, Socket, Payment, and other
capabilities without modifying core SDK code for each addition.

## Decision

Introduce a first-class plugin SPI in `skone-plugin`:

- `SKPlugin` — identity (`id`, `version`) + lifecycle (`onAttach`, `onDetach`)
- `SKPluginContext` — logger and typed dependency lookup (no reflection scanning)
- `SKPluginRegistry` — register / unregister / query plugins
- Default in-memory registry implementation

`SKOne.initialize` accepts an initial plugin list and exposes the registry for
runtime registration.

Plugins are explicit: apps or feature modules register them. No classpath scanning
and no reflection-based discovery in foundation.

## Consequences

- Extensibility without core forks.
- Deterministic lifecycle and testability.
- Apps must register plugins intentionally (acceptable DX trade-off for stability).
- Future optional auto-registration helpers must remain opt-in and documented.
