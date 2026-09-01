# Alpha release checklist

**Current `VERSION_NAME`:** `1.4.0-alpha01` (unchanged — do not bump until an explicit release decision).

**Purpose:** Track consumer readiness for the **next** alpha that would publish the in-tree widget / Application Example surface.

Legend: **READY** · **BLOCKED** · **DEFERRED**

---

## A. Published alpha01 verification (Maven Central)

| Item | Status | Notes |
|------|--------|-------|
| Version on Central | READY | `1.4.0-alpha01` |
| Demo consumer (BOM + compose, no project/mavenLocal) | READY | `samples/skone-demo` |
| Flagship Compose APIs (`SKTheme`, `SKText`, `SKTextField`) | READY | |
| Flagship XML APIs (`SKTextView`, `SKTextFieldView`, theme helper) | READY | |
| Consumer OptIn-free call sites | READY | Public widgets do not require `@OptIn` |
| Docs for published surface | READY | GETTING_STARTED + CONSUMER_GUIDE layer A |

---

## B. Current in-tree verification

| Item | Status | Notes |
|------|--------|-------|
| Application Examples (3 flows) | READY | Playground Samples |
| Compose unit + connected tests | READY | See latest CI/local report |
| XML public views for app chrome | READY | Scaffold / sheet / search / nav / … |
| XML recipes documented | READY | [XML_APPLICATION_RECIPES.md](../architecture/XML_APPLICATION_RECIPES.md) — `bind(runtime, form)` corrected |
| Screen composition guide | READY | SCREEN_COMPOSITION.md |
| Alpha02 cut plan | READY | [ALPHA02_RELEASE_PLAN.md](ALPHA02_RELEASE_PLAN.md) |
| Accidental playground deps in libraries | READY | None found |
| Docs honesty (published vs in-tree) | READY | CONSUMER_GUIDE / checklist |

---

## C. Next alpha release readiness (technical)

| Item | Status | Notes |
|------|--------|-------|
| Module list / BOM constraints | READY | 8 libs + bom |
| `verifyPublishing` / POM metadata | READY | Local verify PASS; signing SKIP without `SIGNING_KEY` (expected) |
| Sources + Dokka javadoc JAR config | READY | Publishing convention |
| `publish.yml` pipeline shape | READY | Unchanged; opt-in Central |
| Public API audit (new widgets) | READY | No consumer-blocking redesign required |
| Binary compatibility vs alpha01 | DEFERRED | Additive surface expected; run japicmp/ABI tool on bump if available |
| VERSION_NAME bump to alpha02 | DEFERRED | Explicit human decision only |
| Maven Central publish attempt | **BLOCKED** | Org **File Count quota** 1050 / 1000 (see below) |

### Maven Central organization quota

| Metric | Limit | Observed |
|--------|-------|----------|
| File Count | 1000 | **1050 / 1000 — BLOCKED** |
| Release Count | 7 | 5 / 7 |
| Release Size | 80 MB | ~25.48 MB / 80 MB |

**Technical release readiness:** PASS (for a future alpha that includes the in-tree surface)  
**Repository publishing availability:** **BLOCKED by Maven Central File Count quota**

This is **not** a library/source failure. Do not repeatedly attempt publish to “test” Central.

---

## Public API audit summary

| Finding | Severity | Action |
|---------|----------|--------|
| In-tree widgets absent from Central alpha01 | P0 consumer trust | Document clearly; publish later as new alpha |
| Docs listing widgets without “in-tree” banner | P0/P1 | Fixed via CONSUMER_GUIDE + checklist honesty |
| Compose `@file:OptIn(SKInternal)` | None | Implementation only; call sites OptIn-free |
| XML `SK*View` public setters | None | Public types only |
| Sync `SKFormController.submit` | P2 | Host owns async; document |
| No ListItem trailing slot / tooltip anchor | P2 | Compose locally |

No breaking redesign performed in this milestone.

---

## Verification commands (local)

```bash
./gradlew test
./gradlew dokkaHtmlAll
./gradlew verifyPublishing
./gradlew :skone-compose:connectedDebugAndroidTest
./gradlew :samples:skone-demo:assembleDebug
./gradlew :samples:skone-playground:assembleDebug
git diff --check
```

Record exact connected totals + device when run.

---

## Deferred (must not block alpha)

- Lazy / paging list primitive  
- Navigation / overlay / form frameworks  
- Playground Material3 shell → SK chrome dogfooding  
- Animation / Material gesture parity  
- Aggressive aesthetic API renames  

---

## Release decision gate

Choose **one**:

| Code | Meaning |
|------|---------|
| **A** | READY FOR alpha02 (version bump + publish allowed) |
| **B** | READY FOR alpha02 **BUT** Maven Central quota blocks publish |

**Current recommendation:** **B** — see [ALPHA02_RELEASE_PLAN.md](ALPHA02_RELEASE_PLAN.md) and [ALPHA02_CONSUMER_READINESS_REPORT.md](ALPHA02_CONSUMER_READINESS_REPORT.md). In-tree consumer surface is ready; **do not bump or publish** until quota is cleared and a human explicitly bumps `VERSION_NAME`.

Maven Central File Count quota remains the external publishing blocker.
