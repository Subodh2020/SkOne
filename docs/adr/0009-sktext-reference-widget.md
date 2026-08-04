# ADR 0009: SKText Reference Widget

## Status

Accepted

## Context

The design system and component framework are ready. The first production widget must
establish coding standards for every future SKOne widget: token-only visuals, dual
Compose/XML surfaces, accessibility defaults, lifecycle/analytics/plugin integration,
and binary-compatible APIs.

## Decision

1. Introduce `skone-ui` for shared, UI-framework-agnostic widget contracts
   (`SKTextComponent`, annotated text model, overflow/align enums).
2. Implement:
   - Compose: `io.skone.compose.widget.SKText`
   - XML: `io.skone.xml.widget.SKTextView`
3. Visuals resolve exclusively through `SKAppearanceConfig` + `SKTheme` tokens.
4. Parameter order follows [SDK API Guidelines](../SDK_API_GUIDELINES.md).
5. Version line: **1.2.0**.

## Consequences

- SKText is the reference implementation for subsequent widgets (`SKButton`, …).
- `skone-ui` stays free of Compose/View types.
- Rich text uses portable `SKAnnotatedText`, mapped by bridges.
- Screenshot tests use Paparazzi (JVM); Compose UI tests live in `androidTest`.
