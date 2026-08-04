# ADR 0007: Design System Theme Engine

## Status

Accepted

## Context

Foundation (v0.1 / ADR 0005) shipped token **interfaces** only. Widgets must not
hardcode colors, spacing, or typography. We need a concrete theme engine, default
token tables, size/shape/state systems, and shared component contracts before any
production widgets ship.

Compose and XML must share the same abstractions without forcing Compose onto
XML consumers (or the reverse).

## Decision

1. Expand `skone-theme` with:
   - Default light/dark token implementations
   - `SKSize` / `SKSizeTokens`, `SKShape` / `SKShapeTokens`
   - `SKComponentState` / `SKInteractionState`
   - `SKTheme`, `SKThemeBuilder`, `SKThemes`, `SKDefaultThemeProvider`

2. Expand `skone-core` component contracts:
   - `SKAppearanceConfig`, `SKBehaviorConfig`, `SKValidationConfig`
   - Expanded accessibility and AI component configs
   - Role enums referencing theme tokens (not raw values)

3. Introduce bridge modules **without widgets**:
   - `skone-compose` — CompositionLocal theme + type converters
   - `skone-xml` — Context-scoped theme helper + converters

4. Version the platform as **1.1.0** for this milestone.

## Consequences

- All future widgets inherit stable contracts and resolve visuals via tokens.
- Apps can supply custom themes without forking the SDK.
- Two small bridge AARs keep framework types out of `skone-theme` / `skone-core`.
- Dynamic Color remains an optional provider concern, not a core token dependency.
