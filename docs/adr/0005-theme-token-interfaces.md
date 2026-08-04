# ADR 0005: Theme Token Interfaces

## Status

Accepted

## Context

SKOne needs a design system usable from Compose and XML without forcing Compose
onto non-UI modules. Shipping concrete Compose `Color`/`Dp` types in foundation
would create an unwanted dependency.

## Decision

Ship design token **interfaces** only in `skone-theme`:

- Token category interfaces (color, typography, spacing, elevation, radius, motion, icons)
- Aggregate `SKThemeTokens`
- `SKThemeMode` and `SKThemeProvider`

Use primitive-backed or abstract token types that do not require Compose.
Compose and XML bridges are deferred to `skone-compose` / `skone-xml`.

## Consequences

- Theme contracts are shared without UI framework coupling.
- Slight indirection until bridges land.
- Implementations can evolve behind stable interfaces.
- Documentation in `DESIGN_SYSTEM.md` is the semantic source of truth.
