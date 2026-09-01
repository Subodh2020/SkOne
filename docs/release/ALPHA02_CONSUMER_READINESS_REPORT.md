# Alpha02 Consumer Readiness Report

**Date:** 2026-09-01  
**Current `VERSION_NAME`:** `1.4.0-alpha01` (unchanged — no bump/publish/commit/tag/push)  
**Intended cut:** `1.4.0-alpha02`  
**Quality bar:** A normal Android developer can build Application Examples A/B/C from public APIs without fighting the library.

---

## 1. Verdict

**TECHNICALLY READY** to bump/publish `1.4.0-alpha02` once Maven Central File Count quota is cleared.

| Gate | Result |
|------|--------|
| Library engineering readiness | **PASS** |
| External Maven consumption of full A/B/C surface | **BLOCKED** — `1.4.0-alpha02` not on Central (404) |
| Published alpha01 Compose + XML smoke | **PASS** (after demo AppCompat theme fix) |
| In-tree Application Examples A/B/C (runtime) | **PASS** on emulator |
| Remaining engineering P0/P1 in library APIs | **None demonstrated** |

**READY TO BUMP:** `1.4.0-alpha02`  
**External blocker:** Maven Central File Count quota (documented 1050/1000) — not a library defect.

---

## 2. P0

| ID | Finding | Evidence | Disposition |
|----|---------|----------|-------------|
| P0-1 | `skone-bom:1.4.0-alpha02` unavailable on Maven Central | HTTP 404; Gradle `Could not find …:1.4.0-alpha02` for `consumers/skone-consumer-hardening` | **External ops.** Do not workaround with `mavenLocal()` / `project()`. |
| P0-2 | Maven Central demo XML path crashed | `IllegalStateException: You need to use a Theme.AppCompat theme` opening `XmlDemoActivity` | **Fixed** in sample + docs (not a library API redesign). |

---

## 3. P1

| ID | Finding | Evidence | Disposition |
|----|---------|----------|-------------|
| P1-1 | XML recipes previously stacked multi-child chrome into `FrameLayout` scaffold slots | Prior consumer hardening; overlapping layout if followed literally | **Already fixed** in recipes + scaffold docs + regression test |
| P1-2 | XML `AppCompatActivity` theme requirement undocumented | Demo crash P0-2 | **Fixed** in `CONSUMER_GUIDE`, `GETTING_STARTED`, `XML_APPLICATION_RECIPES` |
| P1-3 | `setHeaderTitle` vs `setHeadline` discoverability | Prior hardening | **Documented**; no rename (binary/source risk) |

No remaining open P1 library defects demonstrated in this pass.

---

## 4. P2 / Deferred

- Widget doc pages often omit an explicit “in-tree / not on Central yet” banner (core guides are clear).
- XML setter prefixes vs Compose param names (`setBarTitle`, `setSkText`, …).
- Compose slots vs XML `*Container` + host `ScrollView`/`LinearLayout` glue.
- Form wiring: `ProvideSKFormController` vs `bind(runtime, form)`.
- Default no-op icon provider placeholders.
- Host-owned snackbar visibility (no queue / auto-dismiss).
- Dropdown near TopAppBar requires host `Box` / `showAsDropDown(anchor)`.
- LazyList wrapper / animation frameworks — explicitly out of scope.

---

## 5. Public API Changes

**None** in this readiness pass.

- No new widgets.
- No public signature redesigns.
- Demo theme + documentation only for P0-2 / P1-2.

---

## 6. Compose Consumer Validation

### Published alpha01 (`samples/skone-demo`, Maven Central)

- Resolves `skone-bom:1.4.0-alpha01` + `skone-compose`.
- Runtime: home shows `Version: 1.4.0-alpha01`, Central resolve message, live `SKTextField`.
- **Scope:** Text/TextField/theme/forms only — **not** Scaffold/Sheet/Nav.

### Intended alpha02 / in-tree (`samples/skone-playground` Application Examples)

Runtime on `Pixel_9_Pro(AVD) - 16`:

| Flow | Result |
|------|--------|
| A List + Search + Filter | PASS — list rows, error→Retry, Filters sheet (disabled segment/checkbox) |
| B Form + Validation | PASS — empty Save shows field Required + snack “Fix validation errors before saving” |
| C App Shell + Navigation | PASS — Home chrome + destination switch to Activity |

Host-owned state; no custom SKOne frameworks.

### Standalone hardening app Compose A/B/C

Source present under `consumers/skone-consumer-hardening/` targeting BOM `1.4.0-alpha02`.  
**Assemble FAIL** until P0-1 (expected).

---

## 7. XML Consumer Validation

### Published alpha01

- After theme fix: `XmlDemoActivity` shows `SKOne XML Demo (Maven Central)` + Email field; **0 FATAL**.
- APIs used: `SKThemeHelper`, `SKTextView`, `SKTextFieldView` only.

### alpha02-only / not available in published alpha01

`SKScaffoldView`, `SKBottomSheetView`, `SKButtonView`, `SKNavigationBarView`, `SKSearchBarView`, `SKEmptyStateView`, … (full recipes in `XML_APPLICATION_RECIPES.md`).

### Hardening XML A/B/C

Implemented against intended alpha02 public setters; **blocked from Maven build** by P0-1. Consumer already uses `Theme.AppCompat` + LinearLayout chrome wrappers.

---

## 8. Application Examples

Quality bar question: *Can a normal Android developer build these screens without fighting SKOne?*

**YES** (in-tree public APIs) — confirmed by playground runtime + prior composition metrics in `ALPHA02_RELEASE_PLAN.md`.

Do **not** add widgets for host ScrollView/LinearLayout glue or overlay placement conventions.

---

## 9. Accessibility

- Public widgets accept `SKAccessibilityConfig` (testTag / contentDescription / role).
- Application Examples and hardening screens set testTags on key surfaces.
- Compose icon-only controls require CD; XML applies when present (documented intentional difference).
- Connected Compose suite includes a11y-oriented coverage (**94/94**).
- No new a11y API hole demonstrated this pass.

---

## 10. Maven/BOM Verification

### Boundary matrix

| Area | Status | Evidence | Severity |
|------|--------|----------|----------|
| `VERSION_NAME` | `1.4.0-alpha01` | `gradle.properties` | — |
| Root modules | Library + samples only | `settings.gradle.kts` — **no** `consumers/*` | — |
| BOM constraints | All publishable modules | `skone-bom/build.gradle.kts` | — |
| Demo Maven-only | PASS | `samples/skone-demo` BOM alpha01; no `project`/`mavenLocal` | — |
| Hardening Maven-only | Policy PASS / resolve FAIL | BOM alpha02; google+central; `FAIL_ON_PROJECT_REPOS` | P0-1 |
| Central latest | `1.4.0-alpha01` | maven-metadata.xml | — |
| Central alpha02 | Missing | HTTP 404 | P0-1 |
| Publishing config | READY | `verifyPublishing` PASS | — |
| Auto-publish | Not run | Explicit human / workflow opt-in | — |

### Required consumer artifacts (alpha02 intended)

```
platform(com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02)
+ skone-compose   // brings theme/core/ui/forms/common/plugin
+ skone-xml       // when View/XML path needed
```

BOM also constrains: `skone-common`, `skone-plugin`, `skone-theme`, `skone-core`, `skone-ui`, `skone-forms`.

---

## 11. Test Matrix

| Command / check | Result |
|-----------------|--------|
| `./gradlew clean test` | **PASS** (477 tasks) |
| `./gradlew dokkaHtmlAll` | **PASS** |
| `./gradlew verifyPublishing` | **PASS** |
| `./gradlew :skone-compose:connectedDebugAndroidTest` | **94/94 PASS** — Pixel_9_Pro(AVD) API 16 |
| `:samples:skone-demo:assembleDebug` (+ reinstall after theme fix) | **PASS** |
| `:samples:skone-demo:test` | **PASS** (in validation batch) |
| `:samples:skone-playground:assembleDebug` | **PASS** |
| `consumers/... :app:assembleDebug` | **FAIL** — missing `skone-bom:1.4.0-alpha02` (expected) |
| `git diff --check` | **PASS** (exit 0) |
| Playground A/B/C runtime | **PASS** |
| Demo Compose + XML runtime | **PASS** (post-fix) |

---

## 12. Files Changed (this readiness pass)

- `samples/skone-demo/src/main/res/values/themes.xml` — AppCompat theme for XML activity  
- `samples/skone-demo/src/main/AndroidManifest.xml` — apply `Theme.SKOneDemo.Xml`  
- `samples/skone-demo/.../XmlDemoActivity.kt` — host theme note  
- `docs/CONSUMER_GUIDE.md` — layers wording + AppCompat requirement  
- `docs/GETTING_STARTED.md` — AppCompat requirement  
- `docs/architecture/XML_APPLICATION_RECIPES.md` — AppCompat + availability wording  
- `docs/release/ALPHA02_CONSUMER_READINESS_REPORT.md` — this report  

No library module public API files changed.

---

## 13. Release Safety

- **No** `VERSION_NAME` bump  
- **No** publish / tag / commit / push  
- **No** new widgets / frameworks  
- **No** speculative API redesign  
- Demo remains Maven Central–only for alpha01 verification  

---

## 14. Alpha02 Readiness

| Item | Status |
|------|--------|
| In-tree widget foundation for A/B/C | Complete |
| Compose/XML public surface additive vs alpha01 | Documented |
| Docs separate published / intended / in-tree | Consistent in core guides |
| Consumer hardening project | Present; blocked only by Central absence |
| Technical cut readiness | **READY** |
| Repository publishing availability | **BLOCKED by Maven Central File Count quota** |

Once quota is cleared, the human release step is:

1. Set `VERSION_NAME=1.4.0-alpha02` (sync doc aliases if needed)  
2. Re-run the test matrix above  
3. Publish via existing `publish.yml` (`publish_central=true`)  
4. Point `skone-demo` + confirm `consumers/skone-consumer-hardening` resolve/build  
5. Tag / GitHub release only after Central verify  

No additional engineering phase is required for the widget surface itself.

---

## 15. Recommended Next Action

1. **Ops:** Clear Maven Central File Count quota.  
2. **Human:** Explicitly bump to `1.4.0-alpha02` and publish.  
3. **Verify:** Hardening app `assembleDebug` + device run of A/B/C Compose+XML from Central.  
4. **Do not** start another widget batch — primitives are enough; consumer validation is the gate.

---

## Appendix — Repository audit matrix

| Area | Status | Evidence | Severity |
|------|--------|----------|----------|
| Build cleanliness | PASS | `clean test` | — |
| Public Compose APIs | Stable / additive in-tree | playground + compose sources | — |
| Public XML APIs | Stable / additive in-tree | xml sources + recipes | — |
| Internal API leaks to consumers | None observed | Hardening imports public packages only | — |
| Playground | In-tree proof | project modules | — |
| Demo | Published alpha01 proof | Maven Central | — |
| Hardening | External Maven gate | alpha02 target | P0-1 until publish |
| Release docs | Accurate | ALPHA02 plan + checklist + this report | — |
