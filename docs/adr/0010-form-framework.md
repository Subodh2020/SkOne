# ADR 0010: Form Framework

## Status

Accepted

## Context

Future input widgets (`SKTextField`, `SKEmailField`, pickers, selects, …) need a shared
form runtime for state, validation, formatting, masking, focus order, errors, and AI hooks.
Shipping that logic inside each widget would duplicate behavior and fragment APIs.

## Decision

1. Introduce `skone-forms` (`io.skone.forms`) as the form infrastructure module.
2. Reuse component-level types from `skone-core` (`SKValidationResult`, `SKValidationError`,
   `SKAIComponentConfig`) — do not fork result models.
3. Deliver in **1.2.1** without production form widgets:
   - `SKFormController`, `SKFormState`, field registry, error manager
   - `SKValidationEngine` + `SKValidationRule` hierarchy
   - `SKFormatterEngine` + formatters, `SKInputMask` engine
   - `SKFocusChain`, dynamic form schema **interfaces only**, AI form hooks
4. Production fields arrive in a later milestone and bind to this controller.

## Consequences

- Stable foundation for all form widgets.
- Schema is interface-only (no JSON parser shipped yet).
- Apps can unit-test forms without UI widgets.
