# ADR 0003: AI Provider Abstraction

## Status

Accepted

## Context

AI is a first-class SKOne capability. Hardcoding Gemini, OpenAI, Claude, Ollama,
or Azure would couple the SDK to vendors and block enterprise/custom providers.

## Decision

Define a provider-agnostic AI SPI in `skone-core` (`io.skone.ai`):

- `SKAIProvider` — suspend `complete(request): SKResult<SKAIResponse>`
- `SKAIConfig` — default provider id + provider list (`Disabled` sentinel)
- Request/response DTOs and capability flags (grammar, translation, suggestions,
  voice, summarization, autofill)

No vendor SDKs ship in foundation modules. Concrete providers live in future
`skone-ai-*` modules or app code implementing `SKAIProvider`.

Components receive optional `SKAIComponentConfig` so AI can be attached later
without redesigning widget APIs.

## Consequences

- Vendor-neutral public API.
- Easy custom / on-prem providers.
- Foundation cannot perform AI calls without a registered provider (clear failure).
- Capability negotiation is explicit via flags rather than implicit model probing.
