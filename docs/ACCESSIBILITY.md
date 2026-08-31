# Accessibility capability matrix

Honest contract for SKOne **Compose** (`SKText`, `SKTextField`) and **XML** (`SKTextView`, `SKTextFieldView`) as of the current tree (baseline published `1.4.0-alpha01`, with completed Compose `SKTextField` a11y unification).

**Do not treat a property on `SKAccessibilityConfig` as supported** unless this matrix (and the cited tests/source) say it is consumed by that widget.

Status legend:

| Status | Meaning |
|--------|---------|
| **SUPPORTED** | Implemented and covered by source and/or automated tests |
| **PARTIALLY SUPPORTED** | Some behavior exists; gaps or caveats apply |
| **NOT CURRENTLY SUPPORTED** | Not wired for this surface today |
| **INTERNAL / IMPLEMENTATION DETAIL** | Library plumbing; not a consumer a11y API |
| **FUTURE / FOLLOW-UP** | Known gap; not claimed for the current release |

Evidence lives primarily in:

- Compose: `SKTextFieldAccessibilityTest`, `SKTextFieldContractComposeTest`, `SKTextFieldImeFocusComposeTest`, `SKTextComposeTest`
- Compose source: `skone-compose/.../SKText.kt`, `SKTextField.kt`
- XML: `SKTextViewTest`, `SKTextViewContractTest`, `SKTextFieldViewTest`, `SKTextFieldViewContractTest`
- XML source: `skone-xml/.../SKTextView.kt`, `SKTextFieldView.kt`
- Config: `skone-core/.../SKAccessibilityConfig.kt`

---

## `SKAccessibilityConfig` consumption

| Property | Compose `SKText` | Compose `SKTextField` | XML `SKTextView` | XML `SKTextFieldView` |
|----------|------------------|-----------------------|------------------|------------------------|
| `contentDescription` | **SUPPORTED** (fallback: annotated text) | **SUPPORTED** (fallback: label → hint → `"Text field"`) | **SUPPORTED** (`skContentDescription` / fallback to text) | **SUPPORTED** (`skContentDescription` / fallback label → hint → `"Text field"`; applied to root + `input`) |
| `testTag` | **SUPPORTED** | **SUPPORTED** (primary editable node) | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `stateDescription` | **NOT CURRENTLY SUPPORTED** | **SUPPORTED** (merged with required `"Required"` when `required == true`) | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `role` | **NOT CURRENTLY SUPPORTED** | **PARTIALLY SUPPORTED** (mapped only for known strings: `button`, `checkbox`, `switch`, `radio`/`radiobutton`, `tab`, `image`, `dropdown`/`dropdownlist`; unknown → ignored) | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `heading` | **SUPPORTED** | **SUPPORTED** (applied on primary node) | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `liveRegion` | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `traversalIndex` | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |
| `mergeDescendants` | **SUPPORTED** (passed to `Modifier.semantics`) | **NOT CURRENTLY SUPPORTED** (primary node uses plain `semantics { }`; Column is layout-only) | **NOT CURRENTLY SUPPORTED** | **NOT CURRENTLY SUPPORTED** |

Unused / partial summary:

- **Unused everywhere today:** `liveRegion`, `traversalIndex`
- **Unused on Compose `SKText`:** `stateDescription`, `role`
- **Unused on Compose `SKTextField`:** `mergeDescendants`
- **XML surfaces** only wire **`contentDescription`** from config/attrs; other config fields are stored on the component config but not applied to View APIs

---

## Compose — `SKText`

| Capability | Status | Notes |
|------------|--------|-------|
| `contentDescription` | **SUPPORTED** | Explicit CD, else `annotated.text` (`SKText.kt`) |
| `testTag` | **SUPPORTED** | When non-null (`SKTextComposeTest`) |
| `heading` | **SUPPORTED** | When `accessibility.heading == true` |
| `mergeDescendants` | **SUPPORTED** | Honored on the text semantics node |
| `stateDescription` / `role` / `liveRegion` / `traversalIndex` | **NOT CURRENTLY SUPPORTED** | Present on config only |
| Clickable when `onClick != null` | **PARTIALLY SUPPORTED** | Uses `Modifier.clickable` when enabled; no dedicated Role unless added later via config (role unused on `SKText`) |

---

## Compose — `SKTextField` semantics contract

After a11y unification, the **primary editable node** is `BasicTextField`. The outer `Column` is **layout-only** (no merged a11y owner). UI tests should target `onNodeWithTag` / content description on that primary node.

### Primary editable node

| Capability | Status | Evidence |
|------------|--------|----------|
| `testTag` | **SUPPORTED** | Same node as `SetText` (`testTagAndSetTextAreOnSamePrimaryNode`) |
| `contentDescription` | **SUPPORTED** | Explicit or label/hint/`"Text field"`; never rewritten for required |
| `Error` semantics | **SUPPORTED** | When `visualState == Error`; message from support override / supporting / `"Invalid"` (`errorSemanticsOnPrimaryField`, submit path) |
| `stateDescription` | **SUPPORTED** | Consumer `stateDescription` + `"Required"` when `required` |
| Enabled / disabled | **SUPPORTED** | `enabled = false` → `Disabled` semantics; **no** `SetText` (`disabledFieldAccessibility`) |
| `SetText` | **SUPPORTED** when enabled + editable | Absent when disabled |
| `EditableText` | **SUPPORTED** | Asserted on primary node (`contentDescriptionAndEditableSemantics`) |
| `RequestFocus` / focus | **SUPPORTED** via form + `FocusRequester` | Form `requestFocus` / IME bridge drives Compose focus (`SKTextFieldImeFocusComposeTest`) |
| IME actions | **SUPPORTED** | Next/Previous/Done hooked to form focus chain / clear focus |
| `role` | **PARTIALLY SUPPORTED** | Known string map only |
| `heading` | **SUPPORTED** | Applied on primary node |
| `mergeDescendants` (config) | **NOT CURRENTLY SUPPORTED** | Not read on `SKTextField` |

### Required

| Aspect | Status | Notes |
|--------|--------|-------|
| Visual required marker (`label *`) | **SUPPORTED** | Shown when `required == true` |
| Semantic `stateDescription = "Required"` | **SUPPORTED** | Combined with any consumer `stateDescription` (`requiredFieldAccessibility`) |
| Explicit `contentDescription` preserved | **SUPPORTED** | Required does **not** rewrite CD |
| Broader TalkBack announcement guarantees | **NOT claimed** | Tests lock semantics properties, not device ASR phrasing |

### Disabled

| Aspect | Status | Notes |
|--------|--------|-------|
| `Disabled` semantics | **SUPPORTED** | |
| No `SetText` | **SUPPORTED** | Including unmerged tree scan |
| `testTag` discoverable | **SUPPORTED** | Node still exists / displayed |
| Visual alpha | **SUPPORTED** | Reduced alpha for disabled look |

### Read-only

| Aspect | Status | Notes |
|--------|--------|-------|
| Value preserved | **SUPPORTED** | |
| Production edit path rejects changes | **SUPPORTED** | `onValueChange` early-return when `readOnly` (`readOnlyFieldAccessibility`) |
| Compose removes `SetText` | **NOT claimed** | Tests explicitly allow SetText to still appear; edits must be ignored |

### Validation / supporting text

| Aspect | Status | Notes |
|--------|--------|-------|
| `Error` on primary node | **SUPPORTED** | Manual `form.errors` and `form.submit()` with rules |
| Supporting / error text UI | **SUPPORTED** | Separate `SKText` below the field |
| Supporting node as error owner | **NOT CURRENTLY SUPPORTED** | Error lives on the editable node; supporting is visual/secondary |
| Success visual state | **PARTIALLY SUPPORTED** | Token coloring for supporting text; no dedicated success semantics API |

### IME / focus chain

| Aspect | Status | Notes |
|--------|--------|-------|
| Next / Previous moves Compose focus | **SUPPORTED** | Via `FocusRequester` + form focus (`SKTextFieldImeFocusComposeTest`) |
| Form focus chain drives UI focus | **SUPPORTED** | |
| Skip disabled / readOnly in chain | **NOT CURRENTLY SUPPORTED** | Chain advances to the next registered id; **no** skip-to-next-enabled rule (`imeNextTargetsDisabledNextFieldPerFocusChainContract`) |

### IconSlot caveat

| Aspect | Status | Notes |
|--------|--------|-------|
| Leading/trailing `IconSlot` CD | **PARTIALLY SUPPORTED** / caveat | Each icon `Box` sets `contentDescription` to `key.contentDescription ?: key.key`. That is a **separate** semantics node beside the primary field. Apps may hear an extra announcement (including raw key strings). Prefer meaningful `SKIconKey.contentDescription`, or treat icons as decorative in a future pass (**FUTURE / FOLLOW-UP**). |

---

## XML — confirmed behavior only

Claims below are limited to source + Robolectric tests. There is **no** Compose-style semantics tree or `testTag` API on XML widgets today.

### `SKTextView`

| Capability | Status | Notes |
|------------|--------|-------|
| `contentDescription` from `skContentDescription` | **SUPPORTED** | Attr → config + View CD |
| Fallback CD to text when unset | **SUPPORTED** | `setSkText` / `render` (`SKTextViewTest`, contract test) |
| `testTag` / other `SKAccessibilityConfig` fields | **NOT CURRENTLY SUPPORTED** | |
| Theme via `SKThemeHelper` | **INTERNAL / IMPLEMENTATION DETAIL** for a11y | Affects visuals; not an a11y API |

### `SKTextFieldView`

| Capability | Status | Notes |
|------------|--------|-------|
| CD from explicit `skContentDescription` | **SUPPORTED** | Stored in `SKAccessibilityConfig` |
| CD fallback label → hint → `"Text field"` | **SUPPORTED** | Applied to root **and** `input` (`accessibility_usesLabelAsDescription`) |
| `importantForAccessibility = YES` | **SUPPORTED** | On render |
| Label / hint / supporting text (visual) | **SUPPORTED** | Supporting color follows visual state |
| `required` visual `*` on label | **SUPPORTED** | (`requiredAppendsAsteriskToLabel`); **no** XML `stateDescription` equivalent claimed |
| `enabled` / `readOnly` | **SUPPORTED** (input behavior) | Watcher ignores edits; `isEnabled` / focusable / cursor mirror flags |
| Form bind / validation visual state | **SUPPORTED** | Error/success via component visual state + supporting text |
| `testTag` | **NOT CURRENTLY SUPPORTED** | **FUTURE / FOLLOW-UP** (explicitly out of this doc task) |
| Error / live-region accessibility APIs | **NOT CURRENTLY SUPPORTED** | No View `AccessibilityNodeInfo` error wiring claimed beyond CD + supporting text |
| IME Next/Previous form focus | **PARTIALLY SUPPORTED** | Source wires editor actions to form focus; not covered by the same Compose a11y instrumentation suite |

### Public test / a11y hooks (XML)

| Hook | Status |
|------|--------|
| `android:contentDescription` / `app:skContentDescription` | **SUPPORTED** path via attrs / render |
| `view.input` (EditText escape hatch) | **INTERNAL / advanced** — not an a11y contract; use only if you accept coupling |
| Compose `testTag` equivalent | **NOT CURRENTLY SUPPORTED** |

---

## What this document intentionally does **not** claim

- Screen-reader **wording** or platform TalkBack/VoiceOver phrasing beyond semantic properties asserted in tests
- That `readOnly` removes Compose `SetText`
- That form focus **skips** disabled or read-only fields
- That XML has `testTag`, Error semantics nodes, or full `SKAccessibilityConfig` parity
- That `liveRegion` / `traversalIndex` do anything today
- That IconSlot is silent/decorative by default
- New a11y features (XML `testTag`, live regions, etc.) — those are **FUTURE / FOLLOW-UP**, not part of this documentation task

---

## Related docs & tests

- [SKText widget](WIDGETS_SKTEXT.md)
- [SKTextField widget](WIDGETS_SKTEXTFIELD.md)
- [Form framework](FORM_FRAMEWORK.md)
- Compose androidTest: `SKTextFieldAccessibilityTest`, `SKTextFieldImeFocusComposeTest`, `SKTextFieldContractComposeTest`
- XML unit: `SKTextFieldViewTest`, `SKTextViewTest`
