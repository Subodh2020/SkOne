# Alpha02 release plan

**Status:** Release in progress — `VERSION_NAME=1.4.0-alpha02`. Quota cleared; human authorized bump + publish.

---

## Current alpha01 scope (prior Maven Central)

| Item | Scope |
|------|--------|
| Version | `1.4.0-alpha01` |
| Consumer entry | BOM + `skone-compose` / `skone-xml` |
| Flagship Compose | `SKTheme`, `SKText`, `SKTextField`, forms/runtime |
| Flagship XML | `SKThemeHelper`, `SKTextView`, `SKTextFieldView` |
| Verified by | `samples/skone-demo` (historical) |

---

## Alpha02 scope (this cut)

Ship the **full widget kit** and document Application Examples as the consumer quality bar.

### Modules / artifacts

`com.thesubodhgupta.skone:` `skone-bom`, `skone-common`, `skone-plugin`, `skone-theme`, `skone-core`, `skone-ui`, `skone-forms`, `skone-compose`, `skone-xml` @ **`1.4.0-alpha02`**.

### Compatibility

- Flagship alpha01 APIs remain source-compatible.
- Alpha02 is additive.
- XML setter prefixes unchanged.

---

## Blockers

| Blocker | Type | Status |
|---------|------|--------|
| Maven Central File Count quota | External ops | **CLEARED** (human confirmed) |
| VERSION_NAME bump | Human | **DONE** → `1.4.0-alpha02` |
| Publish / Central verify | In progress | Pending workflow |
| Tag / commit / push | Human | Only if explicitly requested |

### Public API additions (additive vs alpha01)

Compose widgets beyond Text/TextField, including but not limited to:

`SKButton`, `SKIconButton`, `SKCheckbox`, `SKSwitch`, `SKRadioButton`/`SKRadioGroup`, `SKChip`, `SKSlider`, `SKProgressIndicator`, `SKDivider`, `SKCard`, `SKSnackbar`, `SKDialog`/`SKAlertDialog`, `SKTopAppBar`, `SKNavigationBar`, `SKScaffold`, `SKListItem`, `SKSectionHeader`, `SKSearchBar`, `SKEmptyState`, `SKFab`, `SKTabRow`/`SKTabs`, `SKBadge`, `SKAvatar`, `SKMenu`/`SKDropdownMenu`, `SKTooltip`, `SKBottomAppBar`, `SKBottomSheet`, `SKSegmentedButton`

Matching XML `SK*View` / hosts + appearance presets (`Button`, `Scaffold`, `BottomSheet`, …).

### Compatibility statement

- **Flagship alpha01 APIs remain source-compatible** (`SKText`, `SKTextField`, appearance/a11y/forms/theme).
- Alpha02 is an **additive** surface expansion.
- No intentional breaking changes in this cut.
- XML setter prefixes (`setBarTitle`, `setSkText`, …) stay as-is (platform clash avoidance).

### Compose / XML parity statement

Equivalent **product semantics** for Application Examples A/B/C. Intentional hosting differences (Compose `visible` vs XML `show`/`dismiss`; dropdown placement; prefixed setters) are documented in `XML_APPLICATION_RECIPES.md`.

### Accessibility status

- Public widgets accept `SKAccessibilityConfig` (testTag, contentDescription, role, …).
- Icon-only Compose controls **require** explicit CD.
- Application Examples carry screen-level testTags.
- Defer: snackbar auto-dismiss, deeper TalkBack matrix automation.

---

## Application composition metrics (quality bar)

Sources: `samples/skone-playground/.../app/AppSurfaces.kt`, `AppSurfaceLogic.kt`.

### A — List + Search + Filter

| Metric | Value |
|--------|-------|
| Unique SKOne widgets | 17 |
| App helpers | `AppSurfaceLogic.filterDirectory` + sample data / enums |
| Host state vars | ~10 (phase, query, tab, draft/applied filters, sheet, snack, selection) |
| Async | `delay` for load/error simulation |
| Custom UI | Column scroll (no Lazy primitive) |
| Workarounds | `contentSafeDrawing = false` under playground Material shell |
| Fighting the library? | **No** |

### B — Form + Validation

| Metric | Value |
|--------|-------|
| Unique SKOne widgets | 10 |
| App helpers | `SKFormController` + rules; `canSubmitProfile` |
| Host state vars | ~9 (fields, toggles, status, snack, discard, forceFail) |
| Async | Host coroutine around sync `form.submit()` |
| Workarounds | Host owns submit loading (documented) |
| Fighting the library? | **No** |

### C — App Shell + Navigation

| Metric | Value |
|--------|-------|
| Unique SKOne widgets | 17 |
| App helpers | `shellTitle` + `ShellDestination` |
| Host state vars | ~6 (destination, homeTab, menu, snack, tooltip) |
| Workarounds | `Box` for dropdown near TopAppBar; tooltip not anchor-wrapped |
| Fighting the library? | **No** (known overlay placement limits are documented) |

**Overall:** A normal Android developer can build these screens with host-owned state and public APIs without fighting SKOne.

---

## Test matrix (pre-bump)

```bash
./gradlew test
./gradlew dokkaHtmlAll
./gradlew verifyPublishing
./gradlew :skone-compose:connectedDebugAndroidTest
./gradlew :samples:skone-demo:assembleDebug :samples:skone-demo:test
./gradlew :samples:skone-playground:assembleDebug
git diff --check
```

Record exact connected totals + device/API.

---

## Release checklist (after quota cleared)

1. Confirm Maven Central File Count &lt; limit.
2. Human sets `VERSION_NAME=1.4.0-alpha02` (and sync `libs.versions.toml` doc comment if needed).
3. Update CONSUMER_GUIDE / GETTING_STARTED / README / RELEASE_NOTES to claim full surface for alpha02.
4. Re-run full test matrix above.
5. Run publish workflow with Central opt-in (`publish.yml` — do not change unless a real requirement appears).
6. Verify Central artifacts resolve; refresh `skone-demo` against alpha02.
7. Tag / GitHub release only after Central verify.

---

## Commands after quota is cleared

```bash
# 1) Bump (human-edited)
# gradle.properties: VERSION_NAME=1.4.0-alpha02

# 2) Validate
./gradlew test dokkaHtmlAll verifyPublishing
./gradlew :skone-compose:connectedDebugAndroidTest
./gradlew :samples:skone-demo:assembleDebug :samples:skone-playground:assembleDebug
git diff --check

# 3) Publish via GitHub Actions (publish_central=true) — do not loop retries on quota errors
```

---

## Blockers

| Blocker | Type | Status |
|---------|------|--------|
| Maven Central File Count 1050/1000 | External ops | **BLOCKED** |
| VERSION_NAME bump | Human decision | Pending |
| Publish / tag / commit / push | Human decision | Not done |

**Technical release readiness:** PASS  
**Repository publishing availability:** BLOCKED by Maven Central File Count quota
