# ADR 0008: Component Framework

## Status

Accepted

## Context

Design System (ADR 0007) delivered tokens, theme engine, and configuration models.
Production widgets must share a single inheritance / composition model for state,
focus, validation, events, analytics, AI, and plugin hooks — without duplicating
logic per widget and without embedding Compose or XML into `skone-core`.

## Decision

1. Introduce a **Component Framework** in `skone-core` (`io.skone.component.framework`):
   - Contracts: `SKComponent`, `SKInteractiveComponent`, `SKInputComponent<T>`,
     `SKSelectableComponent<T>`, `SKNavigationComponent`, `SKAIComponent`
   - Managers: Focus, Validation, State, Event Dispatcher, Animation (interfaces + defaults)
   - Abstractions: Layout, Icon provider, Analytics hooks, Component plugin lifecycle, DSL

2. UI bases live in bridge modules only:
   - `skone-compose` — `SKComposeComponent` host / remember helpers (no widgets)
   - `skone-xml` — `SKXmlComponent` base View (no widgets)

3. All configs continue to flow through existing models:
   `SKComponentConfig`, appearance, behavior, validation, accessibility, analytics, AI.

4. No production widgets (`SKButton`, `SKText`, …) in this milestone.

## Consequences

- Widgets inherit stable runtime behavior and managers.
- Core remains UI-framework agnostic.
- Binary-compatible additive APIs; managers are interface-first for replacement.
- Component DSL configures contracts without constructing concrete widgets.
