# SKOne Release Notes

## 1.4.0-alpha01

**PRE-RELEASE / PRE-PUBLISH.** Repository `VERSION_NAME` is `1.4.0-alpha01`. This version is **not yet published** to Maven Central. The latest published Central release remains **`1.3.2-alpha05`**. The external demo continues to consume Central `1.3.2-alpha05` until `1.4.0-alpha01` is available on Central.

API hygiene, annotation hygiene, theme default policy, Compose `SKTextField` accessibility unification, IME focus bridge (from 1.3.2-alpha05), and accessibility / Getting Started documentation.

### Highlights

1. **API hygiene** — Flagship public defaults no longer name `@SKInternal` implementation types. Internal defaults (logger, theme provider, form engines, size/shape tokens) resolve inside the library. `SKTheme` provider, `SKFormController.create()`, `SKOneConfig` logger, and `SKComponentRuntime` / `rememberSKComponentRuntime` logger use nullable or interface-typed defaults.
2. **Annotation hygiene** — Confirmed framework plumbing is `@SKInternal` (`SKComponentLifecycle`, `rememberSKComponent`, `Modifier.skLayout`, form registration helpers, `SKThemeHelper.clear`, …). **`SKTextFieldView.input` intentionally remains public** (advanced escape hatch; not Internal).
3. **Theme default policy** — Default implementation token packs (`SKDefaultSizeTokens`, `SKDefaultShapeTokens`, `SKDefaultThemeTokens`, typography/spacing/elevation/radius/motion/icon defaults, …) are `@SKInternal`. **`SKLightColorTokens` / `SKDarkColorTokens` remain public** seed APIs. `SKTheme()` / `SKThemes` consumer usage stays OptIn-free.
4. **SKTextField accessibility** — Primary editable node (`BasicTextField`) owns `testTag`, `contentDescription`, `Error`, and `stateDescription`. Disabled semantics (no `SetText`, tag remains). Required uses visual `*` plus `stateDescription = "Required"` without rewriting explicit CD. Existing readOnly edit-rejection preserved (SetText removal not claimed).
5. **IME focus** — IME Next/Previous follows the form focus chain into Compose `FocusRequester`. **No** disabled/readOnly skip policy.
6. **Accessibility documentation** — [`docs/ACCESSIBILITY.md`](ACCESSIBILITY.md) capability matrix (SUPPORTED / PARTIAL / NOT SUPPORTED / INTERNAL / FUTURE). Linked from README and widget docs.
7. **Getting Started** — [`docs/GETTING_STARTED.md`](GETTING_STARTED.md) restored on `master` with public consumer APIs only (`SKTheme`, `SKText`, `SKTextField`, `SKFormController.create()`, `rememberSKComponentRuntime()`).

### Consumer guidance

- Stable flagship APIs remain usable **without** `@OptIn`.
- Soft source break only if apps **directly named** newly Internal default packs or plumbing.
- Flagship `SKText` / `SKTextField` public signatures are unchanged.
- No formal japicmp/BCV ABI claim.

### Demo

[`samples/skone-demo`](../samples/skone-demo/) remains on **Maven Central `1.3.2-alpha05`** until `1.4.0-alpha01` is published. No `mavenLocal()` or `project(":skone-*")` dependencies.

### Deferred (not in 1.4.0-alpha01)

- XML `testTag` API
- IconSlot accessibility noise (separate CD nodes)
- Focus-chain skip for disabled / readOnly fields
- Unused / partial `SKAccessibilityConfig` properties (`liveRegion`, `traversalIndex`, XML parity for most fields, Compose `SKTextField` `mergeDescendants`, …)

## 1.3.2-alpha05

P1 Compose focus fix. **No public API change.**

### Highlights

- **IME Next → actual Compose focus:** `SKTextField` with `imeAction = Next` still updates `SKFormController` / `SKFocusChain`, and now also moves UI focus to the next registered field via a per-field `FocusRequester`
- The same `focusedId` → `FocusRequester` bridge also covers **Previous** (already wired through the form focus chain; not a separate feature)
- **6 instrumented IME focus contracts** in `SKTextFieldImeFocusComposeTest`
- Compose instrumented regression: **33/33 passed**

### Consumer guidance

- Flagship `SKTextField` signature is unchanged
- Disabled / readOnly fields are **not** skipped by `SKDefaultFocusChain` (unchanged contract)

### Demo

[`samples/skone-demo`](../samples/skone-demo/) consumes **Maven Central `1.3.2-alpha05`** (published). It does not use `mavenLocal()` or `project(":skone-*")` dependencies.

### Not in this release

- Merged accessibility node / `SetText` exposure on Compose fields
- Public factory redesign for APIs whose defaults reference `@SKInternal` types
- Skip-disabled / skip-readOnly focus navigation

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

[`samples/skone-demo`](../samples/skone-demo/) consumes **Maven Central `1.3.2-alpha04`** (published). It does not use `mavenLocal()` or `project(":skone-*")` dependencies.

### Prior

- **1.3.2-alpha03** — Improved Compose `SKTextField` floating-label outlined field UX
