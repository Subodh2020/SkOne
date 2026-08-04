# ADR 0004: Result and Error Model

## Status

Accepted

## Context

Async and fallible operations (plugins, AI, network) need a consistent error
surface that is Kotlin-idiomatic, serializable-friendly, and free of checked
exceptions in public APIs.

## Decision

Use a sealed `SKResult<T>` in `skone-common`:

- `SKResult.Success(value)`
- `SKResult.Failure(error: SKError)`

`SKError` carries:

- `code` — stable machine-readable string
- `message` — human-readable description
- `cause` — optional `Throwable`
- `metadata` — optional string map for diagnostics

Prefer returning `SKResult` from public fallible APIs. Reserve thrown exceptions
for programmer errors (invalid usage after failed preconditions).

## Consequences

- Uniform handling across modules.
- Easy mapping to UI error states later.
- Call sites must handle both branches (encouraged via `when`).
- `code` strings become part of the public contract; document and version carefully.
