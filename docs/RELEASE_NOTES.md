# SKOne Release Notes

## 1.3.2-alpha04

API-boundary and contract-test release. Flagship consumer APIs are unchanged in behavior.

### Highlights

- **API boundary stabilization** with `@SKInternal` and `@SKExperimental` (`RequiresOptIn`)
- **`@SKInternal`** on implementation / test-only types (default engines, default logger/theme provider, test hooks such as `SKOne.resetForTest`, `SKRecordingAnalyticsHook`)
- **`@SKExperimental`** on evolving authoring APIs (component DSL/bases, form schema, XML component hosts)
- **Compose `SKTextField` contract tests** (required/email validation → UI, disabled/readOnly, form submit errors)
- **Theme / runtime Compose contract tests** (`SKTheme` / `skTheme`, `rememberSKComponentRuntime`, `ProvideSKComponentRuntime`)
- **Forms contract tests** (`SKFormController`, validation rules, multi-field errors, revalidation)
- **XML/View contract tests** (`SKThemeHelper`, `SKTextView`, `SKTextFieldView`)
- **Documentation** improvements for Maven Central consumption and runtime/theme setup

### Consumer guidance

- **Stable public APIs remain usable without `@OptIn`.** Typical apps use `SKTheme`, `SKText` / `SKTextField`, `SKFormController.create()`, `SKThemeHelper`, and XML widgets without opting into annotations.
- Apps that **directly reference** newly `@SKInternal` implementation types (for example `SKDefaultThemeProvider`, `SKDefaultLogger`, or `SKDefault*Engine`) may need `@OptIn(SKInternal::class)` or should switch to the public interfaces / omit default parameters.
- This release does **not** claim a binary ABI break for flagship APIs. Soft **source** breaks apply only to direct use of marked-internal types.

### Not fixed in alpha04

- Compose `imeAction = Next` updates form focus-chain state only (does not move Compose UI focus)
- Merged accessibility node / `SetText` exposure on Compose fields
- Public factory redesign for APIs whose defaults reference `@SKInternal` types

### Demo

[`samples/skone-demo`](../samples/skone-demo/) remains on **Maven Central `1.3.2-alpha03`** until `1.3.2-alpha04` is published. Bump the demo coordinates after Central availability.

### Prior

- **1.3.2-alpha03** — Improved Compose `SKTextField` floating-label outlined field UX
