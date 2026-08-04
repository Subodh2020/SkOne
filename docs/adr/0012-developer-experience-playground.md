# ADR 0012: Developer Experience Playground

## Status

Accepted

## Context

SKOne has production widgets (`SKText`, `SKTextField`) and frameworks, but no
official place for developers to browse components, try properties, switch
themes, read docs, or copy Compose/XML snippets. Shipping more widgets without
a showcase would slow adoption.

## Decision

1. Version **1.3.1** delivers a **developer experience layer only** — no new
   production widgets.
2. Official showcase app: `samples:skone-playground` (`io.skone.playground`).
3. Capabilities: component catalog, widget gallery, live property editor,
   Compose/XML codegen, theme switcher, in-app documentation, sample browser,
   search, and navigation.
4. Companion static **docs site** under `docs-site/` for browser-based reading.
5. `samples:skone-sample` remains a minimal integration sample; the playground
   is the canonical showcase for every future component.

## Consequences

- Future widgets must register themselves in the playground catalog registry.
- DX chrome may use Material 3; demos must use SKOne widgets + design tokens.
- Playground is not published as an AAR.
