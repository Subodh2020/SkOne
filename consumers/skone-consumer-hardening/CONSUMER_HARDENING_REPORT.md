# Consumer Hardening Report

**Date:** 2026-09-01  
**Target BOM:** `com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02`  
**Consumer project:** `consumers/skone-consumer-hardening/` (standalone Gradle; not in SKOne module graph)

Quality bar: *A developer unfamiliar with SKOne can build realistic application screens using only the published APIs with minimal custom code.*

---

## A. Consumer readiness

**NOT READY against Maven Central for this milestone.**

| Layer | Status |
|-------|--------|
| External Maven-only consumer project | Created; no `mavenLocal()`, no `project(":skone-*")` |
| Flows A/B/C Compose + XML source | Implemented against public in-tree / intended-alpha02 APIs |
| Resolve `1.4.0-alpha02` from Central | **FAIL** (HTTP 404) |
| Build & device validation of consumer | **Blocked** by F01 |
| Published alpha01 | Can smoke Text/TextField only (`samples/skone-demo`); insufficient for A/B/C |

In-tree playground Application Examples remain the proof for the full widget surface until alpha02 is published.

---

## B. P0 findings

1. **`1.4.0-alpha02` missing on Maven Central** — consumer cannot resolve `skone-compose` / `skone-xml` at the required version. Latest Central release: `1.4.0-alpha01`.  
   - Fix path: clear Maven Central file-count quota → human bump/publish (out of scope for this agent unless explicitly requested).  
   - No library code fix substitutes for publish.

---

## C. P1 findings

1. **XML scaffold multi-child chrome docs** — recipes stacked top bar + tabs (and bottom bar + nav) into `FrameLayout` slots → overlapping UI if followed literally.  
   - **Fix applied:** docs (`XML_APPLICATION_RECIPES.md`, `WIDGETS_SKSCAFFOLD.md`) + regression test `scaffold_multiChildChrome_requiresHostWrapper`.  
   - **API not redesigned** (changing `FrameLayout` → `LinearLayout` would break binary property types).

2. **XML title setter discoverability** — `SKSectionHeaderView.setHeaderTitle` vs `SKListItemView.setHeadline` vs Compose `title`/`headline`.  
   - **Fix applied:** recipe clarification. No rename.

---

## D. P2 findings

- Compose slots vs XML container + ScrollView/LinearLayout glue  
- XML setter prefixes (`setBarTitle`, `setSkText`, `setBadgeText`, …)  
- Form wiring: `ProvideSKFormController` vs `bind(runtime, form)`  
- Default no-op icon provider placeholders  
- Host-owned snackbar visibility (no queue)

---

## E. APIs that felt awkward

- XML `FrameLayout` chrome slots for multi-child stacks (documented; host wrapper required)  
- `setHeaderTitle` / `setSelectedState` / `setBadgeText` naming vs Compose  
- Rebuilding XML list body on every query keystroke (host pattern; no LazyList wrapper — correctly deferred)

---

## F. Compose/XML parity findings

| Behavior | Parity |
|----------|--------|
| Host-owned list/form/shell state | Yes |
| Loading / empty / error+retry | Yes |
| Sheet / dialog / snackbar / nav | Yes (show/dismiss vs visible) |
| Forms validation | Yes (shared `SKFormController`) |
| testTags via `SKAccessibilityConfig` | Yes |
| Scaffold composition model | Intentional difference (slots vs containers) |

No new parity gaps that justify a widget or API redesign.

---

## G. Accessibility findings

- Flows pass `contentDescription` on icon keys and `testTag` on interactive surfaces.  
- Compose icon-only controls require CD; XML applies when present (documented intentional difference).  
- No new a11y crash or missing role demonstrated beyond existing widget behavior.  
- **No P0/P1 a11y API fix applied** (none demonstrated against runnable Central artifacts).

---

## H. Documentation findings

- Updated scaffold multi-child guidance and section-header setter note.  
- Consumer README + friction log describe Central blocker and policy.  
- Remaining gap: Central publish/checklist still owns alpha02 cut (see `docs/release/ALPHA02_RELEASE_PLAN.md`).

---

## I. New tests

| Test | Location |
|------|----------|
| `ConsumerLogicTest` | `consumers/skone-consumer-hardening/app/src/test/...` |
| `scaffold_multiChildChrome_requiresHostWrapper` | `skone-xml/.../SKContentXmlTest.kt` |

Instrumented Compose/XML UI tests for the external consumer are blocked until alpha02 resolves.

---

## J. Files changed

**Consumer (new):**
- `consumers/skone-consumer-hardening/**` (standalone app, Compose A/B/C, XML A/B/C, tests, README, friction, this report)

**Library / docs (P1 only):**
- `docs/architecture/XML_APPLICATION_RECIPES.md`
- `docs/WIDGETS_SKSCAFFOLD.md`
- `skone-xml/src/test/java/io/skone/xml/widget/SKContentXmlTest.kt`

**Not changed:** `VERSION_NAME`, publish workflows, public widget APIs, no new widgets.

---

## K. Whether any new widget is genuinely justified

**No.** Flows A/B/C compose from existing Scaffold / Search / Empty / Sheet / Form / Nav / Dialog / Snackbar primitives. Host ScrollView/LinearLayout glue and Central publish are the blockers — not missing widgets.

---

## Validation

| Check | Result |
|-------|--------|
| Maven Central `skone-bom:1.4.0-alpha02` | **404 / not found** |
| Consumer `./gradlew :app:assembleDebug` | **FAIL** — `Could not find com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02` |
| `:skone-xml:testDebugUnitTest` (incl. scaffold wrapper regression) | PASS |
| `:skone-ui` / `:skone-forms` / `:skone-core` / `:skone-theme` / `:skone-common` / playground unit tests | PASS |
| `:samples:skone-demo:assembleDebug` | PASS |
| `verifyPublishing` | PASS |
| `dokkaHtmlAll` | PASS |
| `:samples:skone-playground:assembleDebug` | PASS |
| `:skone-compose:connectedDebugAndroidTest` | **94/94 PASS** (Pixel_9_Pro API 16 emulator) |

No new widget. No `VERSION_NAME` bump. No publish.
