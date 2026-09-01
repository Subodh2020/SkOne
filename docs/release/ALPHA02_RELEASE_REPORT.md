# Alpha02 Release Report — Commit/Push Complete

**Date:** 2026-09-01  
**Version:** `1.4.0-alpha02`  
**Branch:** `master` → `origin/master`

---

## Commit / push

| Item | Value |
|------|--------|
| Commit | `1f8cd25a8c0616addc2829db73ef4388df18e7ec` |
| Message | `Release SKOne 1.4.0-alpha02` |
| Pushed branch | `origin/master` |
| Remote verify | `HEAD` == `origin/master` == `1f8cd25` |
| Tag / GitHub Release | **Not created** |
| `publish.yml` | **Not dispatched** |

---

## Files committed

**189 files** · **+24638 / −230**

Top-level breakdown:

| Area | Files |
|------|------:|
| docs | 47 |
| skone-compose | 36 |
| skone-xml | 35 |
| skone-ui | 26 |
| consumers | 25 |
| samples | 14 |
| skone-core | 2 |
| gradle.properties / libs.versions.toml / README / docs-site | 4 |

Included: widgets, tests, playground Application Examples, consumer hardening app, release docs, `VERSION_NAME` / `skone.version` / `libs.versions.toml`.

Excluded: `local.properties`, `.gradle/`, build outputs, IDE files, secrets.

---

## Validation results (pre-commit)

| Check | Result |
|-------|--------|
| `clean test` | PASS |
| `dokkaHtmlAll` | PASS |
| `verifyPublishing` | PASS |
| `:skone-compose:connectedDebugAndroidTest` | **94/94 PASS** (Pixel_9_Pro API 16) |
| `:samples:skone-demo:assembleDebug` | PASS |
| `:samples:skone-playground:assembleDebug` | PASS |
| `git diff --check` | PASS |

---

## Version / BOM

| Item | Value |
|------|--------|
| `VERSION_NAME` | `1.4.0-alpha02` |
| `skone.version` | `1.4.0-alpha02` |
| `libs.versions.toml` `skone` | `1.4.0-alpha02` |
| Publications (9/9) | `skone-bom`, `skone-common`, `skone-plugin`, `skone-theme`, `skone-core`, `skone-ui`, `skone-forms`, `skone-compose`, `skone-xml` → **1.4.0-alpha02** |

---

## Remaining publishing status

**Maven Central publish is a separate gate — not executed.**

Next (only when you explicitly authorize):

1. Dispatch `.github/workflows/publish.yml` with `publish_central=true` and `create_github_release=false`
2. Wait for Central verify of `skone-bom:1.4.0-alpha02`
3. Build `consumers/skone-consumer-hardening/` from Maven Central only
4. Emulator Compose + XML A/B/C
5. Refresh `samples/skone-demo` to alpha02

Until then, Central latest remains **`1.4.0-alpha01`**; alpha02 exists on `origin/master` but is **not yet published**.
