# ADR 0011: SKTextField Flagship Input

## Status

Accepted

## Context

Form Framework (1.2.1) and Component Framework are ready. The first production input
widget must demonstrate form registration, validation, formatting, masking, focus,
tokens, accessibility, analytics, plugins, and AI — as the standard for all future
inputs (`SKPasswordField`, `SKEmailField`, …).

## Decision

1. Shared contract: `SKTextFieldComponent` in `skone-ui` (depends on `skone-forms`).
2. Surfaces:
   - Compose `io.skone.compose.widget.SKTextField`
   - XML `io.skone.xml.widget.SKTextFieldView`
3. When an `SKFormController` is provided (CompositionLocal / bind), the field
   **auto-registers** and routes input through ValidationEngine, FormatterEngine,
   InputMask, and FocusChain.
4. Visuals resolve only via `SKAppearanceConfig` + theme tokens.
5. Version: **1.3.0**.

## Consequences

- SKTextField is the reference input implementation.
- Standalone mode works without a form controller (local engine fallback).
- Specialized fields should extend / compose this pattern, not fork it.
