# Accessibility capability matrix

Honest contract for SKOne **Compose** (`SKText`, `SKTextField`) and **XML** (`SKTextView`, `SKTextFieldView`).

**Published baseline:** `1.4.0-alpha01` (Maven Central).
**In-tree development (unreleased):** flagship a11y & dual-surface parity — see [`architecture/NEXT_PHASE_DESIGN.md`](architecture/NEXT_PHASE_DESIGN.md).

**Do not treat a property on `SKAccessibilityConfig` as supported** unless this matrix (and the cited tests/source) say it is consumed by that widget.

Status legend:

| Status | Meaning |
|--------|---------|
| **SUPPORTED** | Implemented and covered by source and/or automated tests |
| **PARTIALLY SUPPORTED** | Some behavior exists; gaps or caveats apply |
| **NOT CURRENTLY SUPPORTED** | Not wired for this surface today |
| **INTERNAL / IMPLEMENTATION DETAIL** | Library plumbing; not a consumer a11y API |
| **FUTURE / FOLLOW-UP** | Known gap; not claimed yet |

Evidence lives primarily in:

- Compose: `SKTextFieldAccessibilityTest`, `SKTextFieldContractComposeTest`, `SKTextFieldImeFocusComposeTest`, `SKTextComposeTest`
- Compose source: `skone-compose/.../SKText.kt`, `SKTextField.kt`, `SKAccessibilitySemantics.kt`
- XML: `SKTextViewTest`, `SKTextViewContractTest`, `SKTextFieldViewTest`, `SKTextFieldViewContractTest`
- XML source: `skone-xml/.../SKTextView.kt`, `SKTextFieldView.kt`, `SKXmlAccessibility.kt`
- Config: `skone-core/.../SKAccessibilityConfig.kt`

---

## `SKAccessibilityConfig` consumption

| Property | Compose `SKText` | Compose `SKTextField` | XML `SKTextView` | XML `SKTextFieldView` |
|----------|------------------|-----------------------|------------------|------------------------|
| `contentDescription` | **SUPPORTED** (fallback: annotated text) | **SUPPORTED** (fallback: label → hint → `"Text field"`) | **SUPPORTED** (`skContentDescription` / fallback to text) | **SUPPORTED** (`skContentDescription` / fallback label → hint → `"Text field"`; root + `input`) |
| `testTag` | **SUPPORTED** | **SUPPORTED** (primary editable node) | **SUPPORTED** (`View.setTag` / `app:skTestTag`) | **SUPPORTED** on **primary `input`** (`View.setTag` / `app:skTestTag`) |
| `stateDescription` | **SUPPORTED** | **SUPPORTED** (merged with required `"Required"` when `required == true`) | **SUPPORTED** (`ViewCompat.setStateDescription`) | **SUPPORTED** on `input` (merged with `"Required"` when required) |
| `role` | **PARTIALLY SUPPORTED** (known string map) | **PARTIALLY SUPPORTED** (same map) | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `heading` | **SUPPORTED** | **SUPPORTED** (primary node) | **SUPPORTED** (`ViewCompat.setAccessibilityHeading`) | **SUPPORTED** on `input` |
| `liveRegion` | **SUPPORTED** (`LiveRegionMode.Polite` when `true`) | **SUPPORTED** on primary node | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `traversalIndex` | **SUPPORTED** | **SUPPORTED** on primary node | **NOT CURRENTLY SUPPORTED** (no safe 1:1 View mapping) | **NOT CURRENTLY SUPPORTED** |
| `mergeDescendants` | **SUPPORTED** | **NOT CURRENTLY SUPPORTED** (preserves primary-node ownership) | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |

Remaining gaps:

- **Compose `SKTextField`:** `mergeDescendants` intentionally unused
- **XML:** `role`, `liveRegion`, `traversalIndex` unused
- **Focus chain:** still does **not** skip disabled / readOnly

---

## Compose — `SKText`

| Capability | Status | Notes |
|------------|--------|-------|
| `contentDescription` | **SUPPORTED** | Explicit CD, else `annotated.text` |
| `testTag` | **SUPPORTED** | `SKTextComposeTest` |
| `heading` | **SUPPORTED** | |
| `mergeDescendants` | **SUPPORTED** | |
| `stateDescription` / `role` | **SUPPORTED** / **PARTIAL** | Role uses known string map (`appliesStateDescriptionAndButtonRole`) |
| `liveRegion` / `traversalIndex` | **SUPPORTED** | Via shared `applyOptionalAccessibility` |
| Clickable when `onClick != null` | **PARTIALLY SUPPORTED** | `Modifier.clickable`; set `role = "button"` when needed |

---

## Compose — `SKTextField` semantics contract

The **primary editable node** is `BasicTextField`. The outer `Column` is layout-only.

### Primary editable node

| Capability | Status | Evidence |
|------------|--------|----------|
| `testTag` | **SUPPORTED** | Same node as `SetText` |
| `contentDescription` | **SUPPORTED** | Never rewritten for required |
| `Error` semantics | **SUPPORTED** | |
| `stateDescription` | **SUPPORTED** | Consumer + `"Required"` |
| Enabled / disabled | **SUPPORTED** | |
| `role` / `heading` / `liveRegion` / `traversalIndex` | **PARTIAL** / **SUPPORTED** | Role map only; mergeDescendants **not** applied |
| `mergeDescendants` | **NOT CURRENTLY SUPPORTED** | Would fight primary-node model |

### IconSlot

| Aspect | Status | Notes |
|--------|--------|-------|
| Decorative by default | **SUPPORTED** | Null/blank `SKIconKey.contentDescription` → `clearAndSetSemantics { }` (no raw-key announcement) |
| Explicit icon CD | **SUPPORTED** | Non-blank CD creates a separate announceable node (`explicitIconContentDescriptionIsAnnouncedSeparately`) |

### IME / focus chain

| Aspect | Status | Notes |
|--------|--------|-------|
| Next / Previous moves Compose focus | **SUPPORTED** | |
| Skip disabled / readOnly in chain | **NOT CURRENTLY SUPPORTED** | Contract unchanged; opt-in skip is a future decision |

---

## XML — `SKTextView`

| Capability | Status | Notes |
|------------|--------|-------|
| `contentDescription` | **SUPPORTED** | Attr / `setAccessibility` / text fallback |
| `testTag` | **SUPPORTED** | `View.tag` + `app:skTestTag` (`accessibility_appliesTestTagHeadingAndStateDescription`) |
| `stateDescription` / `heading` | **SUPPORTED** | ViewCompat |
| `setAccessibility(SKAccessibilityConfig)` | **SUPPORTED** | Additive public API |
| `role` / `liveRegion` / `traversalIndex` | **NOT CURRENTLY SUPPORTED** | |

---

## XML — `SKTextFieldView`

| Capability | Status | Notes |
|------------|--------|-------|
| CD root + `input` | **SUPPORTED** | Existing contract retained |
| `testTag` on **primary `input`** | **SUPPORTED** | Root does **not** own the automation tag |
| `stateDescription` / `heading` on `input` | **SUPPORTED** | Required merges `"Required"` without rewriting CD |
| Error on `AccessibilityNodeInfo` | **SUPPORTED** | When `visualState == Error` (`accessibility_errorExposesNodeInfoError`) |
| Leading / trailing icons render | **SUPPORTED** | Decorative by default; explicit CD announces |
| `setAccessibility` / `app:skTestTag` | **SUPPORTED** | Additive |
| `role` / `liveRegion` / `traversalIndex` | **NOT CURRENTLY SUPPORTED** | |
| IME Next/Previous form focus | **PARTIALLY SUPPORTED** | Wired in source; thinner instrumentation than Compose |

### Public hooks

| Hook | Status |
|------|--------|
| `app:skContentDescription` / `app:skTestTag` | **SUPPORTED** |
| `setAccessibility(...)` | **SUPPORTED** |
| `view.input` | Public escape hatch (unchanged); prefer config APIs for a11y |

---

## What this document intentionally does **not** claim

- Screen-reader **wording** beyond properties asserted in tests
- That `readOnly` removes Compose `SetText`
- That form focus **skips** disabled or read-only fields
- That Compose TextField honors `mergeDescendants`
- That XML maps `role` / `liveRegion` / `traversalIndex`
- That this work is published on Maven Central (development after `1.4.0-alpha01`)

---

## Related docs & tests

- [Next phase design](architecture/NEXT_PHASE_DESIGN.md)
- [SKText widget](WIDGETS_SKTEXT.md)
- [SKTextField widget](WIDGETS_SKTEXTFIELD.md)
- [SKButton widget](WIDGETS_SKBUTTON.md)
- [Form framework](FORM_FRAMEWORK.md)
- Compose androidTest: `…`, `SKOverlayNavigationComposeTest`, `SKContentComposeTest`, …
- XML unit: `…`, `SKOverlayNavigationXmlTest`, `SKContentXmlTest`

---

## Compose / XML — `SKButton` (in-tree)

| Capability | Status |
|------------|--------|
| Default role `button` | **SUPPORTED** |
| `contentDescription` / `testTag` / `stateDescription` | **SUPPORTED** |
| Disabled semantics | **SUPPORTED** |
| Loading → `stateDescription` includes `"Loading"`; clicks blocked | **SUPPORTED** |
| Leading icon decorative-by-default | **SUPPORTED** (same IconSlot rule) |
| Focus-chain skip | N/A (not a form field) |

### `SKCheckbox` / `SKSwitch` / `SKIconButton` (in-tree)

| Capability | Status |
|------------|--------|
| Checkbox/Switch toggleable state | **SUPPORTED** |
| Checked/On stateDescription | **SUPPORTED** |
| Disabled | **SUPPORTED** |
| IconButton explicit CD required (Compose) | **SUPPORTED** — never raw key |
| Checkbox indeterminate | **DEFERRED** |

### `SKRadioButton` / `SKRadioGroup` / `SKChip` (in-tree)

| Capability | Status |
|------------|--------|
| Radio role + selected | **SUPPORTED** |
| RadioGroup exclusive selection | **SUPPORTED** |
| Chip selected + button role | **SUPPORTED** |
| Chip leading icon decorative-by-default | **SUPPORTED** |
| Filter/input/assist chip variants | **DEFERRED** |

### `SKSlider` / `SKProgressIndicator` / `SKDivider` / `SKCard` (in-tree)

| Capability | Status |
|------------|--------|
| Slider progress/range semantics + value | **SUPPORTED** |
| Progress linear + circular | **SUPPORTED** |
| Divider decorative (no TalkBack) | **SUPPORTED** |
| Card surface + optional click/button role | **SUPPORTED** |
| Range / dual-thumb / vertical slider | **DEFERRED** |
| Specialized Material card variants | **DEFERRED** |

### Overlay / navigation (in-tree)

| Capability | Status |
|------------|--------|
| Snackbar live region + optional action | **SUPPORTED** |
| Dialog / AlertDialog host-controlled visibility | **SUPPORTED** |
| TopAppBar icon CD required | **SUPPORTED** |
| NavigationBar selected Tab semantics | **SUPPORTED** |
| Overlay manager / snackbar queue | **DEFERRED** |
| Bottom sheet | **DEFERRED** |

### Content shell (in-tree)

| Capability | Status |
|------------|--------|
| ListItem click/selected/disabled + ellipsis | **SUPPORTED** |
| ListItem decorative leading icon | **SUPPORTED** |
| SectionHeader heading + optional action | **SUPPORTED** |
| Scaffold top/content/bottom + snackbar + optional FAB slot | **SUPPORTED** |
| Safe-drawing / system-bar insets helper | **SUPPORTED** (`skSafeDrawingPadding` / `skApplySystemBarPadding`) |
| Snackbar queue / overlay manager | **DEFERRED** |

### Search / empty / FAB (in-tree)

| Capability | Status |
|------------|--------|
| SearchBar query + clear + IME Search | **SUPPORTED** |
| SearchBar decorative leading icon | **SUPPORTED** |
| SearchBar clear accessible label | **SUPPORTED** |
| EmptyState title/description/actions | **SUPPORTED** |
| EmptyState decorative illustration | **SUPPORTED** |
| FAB required content description | **SUPPORTED** (Compose require; XML incomplete-config like IconButton) |
| FAB scaffold bottom-end slot | **SUPPORTED** (placement only) |
| Search suggestions / history | **DEFERRED** |
| Extended FAB / speed-dial | **DEFERRED** |

### Tabs / Badge / Avatar (in-tree)

| Capability | Status |
|------------|--------|
| TabRow exclusive selection + Role.Tab | **SUPPORTED** |
| Per-tab disabled | **SUPPORTED** |
| Tab decorative icons | **SUPPORTED** |
| Badge count / text announcement | **SUPPORTED** |
| Badge decorative dot (silent by default) | **SUPPORTED** |
| Avatar initials + host image content | **SUPPORTED** |
| Avatar decorative when no CD/initials | **SUPPORTED** |
| Pager / swipe / scrollable tabs | **DEFERRED** |
| Image loading (Coil/Glide) | **DEFERRED** |
| Badge anchoring helpers | **DEFERRED** |

### Menu / Dropdown / Tooltip / BottomAppBar (in-tree)

| Capability | Status |
|------------|--------|
| Menu items + disabled + testTag | **SUPPORTED** |
| Menu decorative leading icons | **SUPPORTED** |
| Dropdown expand/select/dismiss | **SUPPORTED** |
| Tooltip host-controlled visibility (no live region) | **SUPPORTED** |
| BottomAppBar leading/content/trailing slots | **SUPPORTED** |
| BottomAppBar optional FAB layout slot | **SUPPORTED** (layout only) |
| Nested/cascading menus | **DEFERRED** |
| Tooltip manager / hover orchestration | **DEFERRED** |
| BottomAppBar hide-on-scroll | **DEFERRED** |

### BottomSheet / SegmentedButton (in-tree)

| Capability | Status |
|------------|--------|
| BottomSheet host-controlled show/dismiss | **SUPPORTED** |
| BottomSheet primary/secondary actions + disabled | **SUPPORTED** |
| BottomSheet modal dialog/scrim (no drag framework) | **SUPPORTED** |
| SegmentedButton exclusive selection | **SUPPORTED** |
| SegmentedButton per-segment disabled | **SUPPORTED** |
| SegmentedButton radio selected semantics | **SUPPORTED** |
| Material sheet gestures / nested scroll | **DEFERRED** |
| Multi-select segmented control | **DEFERRED** |
