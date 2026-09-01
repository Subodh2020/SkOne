# Screen composition with SKOne

How to build realistic application screens from existing SKOne primitives.

**Audience:** app developers using `skone-compose` / `skone-xml`.  
**Companion:** Playground → **Application Examples**; [XML recipes](XML_APPLICATION_RECIPES.md); [Consumer Guide](../CONSUMER_GUIDE.md).

## Published vs in-tree

| Layer | What you can build |
|-------|--------------------|
| Maven Central `1.4.0-alpha01` | Flagship `SKText` / `SKTextField` (+ theme/forms) — see demo |
| In-tree (this repo) | Full Application Examples (list/filter, form, shell) |

Application Examples are the **quality bar for the next alpha**, not a claim that Central already ships Scaffold/Button/etc.

## Goal

Prove that a developer can assemble a production-style screen **cleanly, consistently, and accessibly** with host-owned state and minimal custom chrome — without inventing new SKOne frameworks.

## State ownership

| Own in the screen / host | Leave to the widget |
|--------------------------|---------------------|
| Visibility (`sheetOpen`, `dialogVisible`, `snackVisible`) | Presentation / tokens |
| Selected ids (tabs, nav, list selection) | Exclusive-selection rendering |
| Form field values + submit UX (loading, success, failure) | Field validation rules via `SKFormController` / field `rules` |
| Loading / empty / error phase | `SKProgressIndicator` / `SKEmptyState` content |
| Navigation destination | `SKNavigationBar` / `SKTabRow` callbacks |

**Rule:** widgets do not own business state. Callbacks are predictable (`onSelect`, `onDismissRequest`, `onCheckedChange`, `onClick`).

The same host model works for Compose and XML: keep values + phase outside the view, push props down, react to callbacks.

## Compose approach

1. Wrap the screen in `SKScaffold` (topBar / content / bottomBar / snackbar / FAB slots).
2. Put chrome widgets in slots (`SKTopAppBar`, `SKNavigationBar`, `SKBottomAppBar`, `SKFab`).
3. Put content in a scrollable column (or your own list host). SKOne does **not** ship a Lazy/paging list yet — compose with platform lists when needed.
4. Drive overlays with host booleans: `SKBottomSheet(visible=…)`, `SKAlertDialog(visible=…)`, `SKDropdownMenu(expanded=…)`, `SKSnackbar(visible=…)`.
5. Pass `SKAccessibilityConfig(testTag = …)` on interactive surfaces.

See playground: `samples/skone-playground/.../app/AppSurfaces.kt`.

## XML approach

Mirror the same product semantics — see **[XML_APPLICATION_RECIPES.md](XML_APPLICATION_RECIPES.md)**.

- Host Activity/Fragment owns state.
- Use `SK*View` setters + listeners.
- Sheets/dialogs: `show()` / `dismiss()` instead of Compose `visible`.
- Forms: `SKTextFieldView.bind(runtime, form)` + the same validation rules.

Intentional differences are **hosting mechanics**, not product behavior.

## Accessibility expectations

- Logical order follows visual order (top bar → content → bottom bar).
- Headings: section headers / empty titles should be discoverable.
- Selection / checked / disabled states come from widget semantics.
- Loading: progress has content/state description.
- Errors: surface via field errors, empty-state copy, or snackbar — avoid duplicate live-region spam.
- Icon-only actions require explicit `contentDescription`.
- Prefer `testTag` on screen-level controls for UI tests.

## Loading / error / empty patterns

| State | Pattern |
|-------|---------|
| Loading | `SKProgressIndicator(indeterminate = true)` (+ optional copy) |
| Empty | `SKEmptyState` with clear / retry actions |
| Error | `SKEmptyState` retry, or snackbar / alert for submit failures |
| Submitting | Host flag + `SKButton(loading = true, enabled = false)` |

Do **not** introduce a global screen-state DSL unless multiple real screens prove the same contract (3+ rule).

## When to use existing primitives

- Lists, search, filters → Scaffold + SearchBar + ListItem + BottomSheet + Checkbox / SegmentedButton
- Settings / profile → TextField + Switch / Checkbox + Button + AlertDialog + Snackbar (+ `SKFormController`)
- App chrome → Scaffold + TopAppBar + NavigationBar + optional BottomAppBar / Tabs / Menu / FAB

## When NOT to create abstractions

Avoid: generic UI DSL, overlay/sheet managers, navigation frameworks inside SKOne, form state machines beyond `SKFormController`, animation frameworks.

**3-screen rule:** one screen → compose locally; two share a need → small helper; three+ → consider a lean library abstraction.

## Playground Application Examples

| Example | Proves |
|---------|--------|
| List + Search + Filter | Search, tabs, sheet filters, disabled option, loading/empty/error, snackbar |
| Form + Validation | Multi-field validate, disabled field, submit loading, success/failure, discard dialog |
| App Shell + Navigation | 3 destinations, tabs, card, list, menu (disabled item), bottom bar, FAB, tooltip |

## Deferred (not blocking app screens)

- Lazy / paging list primitive
- Async `SKFormController.submit` hooks (host currently owns async loading around sync submit)
- `SKListItem` trailing composable slot
- Anchor-style `SKTooltip` wrapper
- Replacing Playground’s Material3 shell with SK chrome
