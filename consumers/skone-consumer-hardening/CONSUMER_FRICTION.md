# Consumer friction log

Recorded while implementing Compose + XML flows A/B/C against **public** SKOne APIs intended for `skone-bom:1.4.0-alpha02`.

Severity: **P0** release blocker · **P1** significant friction/parity/a11y · **P2** polish.

| ID | Severity | Area | Finding | Workaround / fix |
|----|----------|------|---------|------------------|
| F01 | **P0** | Publish | `skone-bom:1.4.0-alpha02` returns 404 on Maven Central; only `1.4.0-alpha01` exists. Consumer app cannot resolve Compose/XML widget surface. | None allowed (`mavenLocal` / `project` forbidden). Human publish after Central quota. |
| F02 | P1 | Docs | XML recipes previously `addView`’d top bar + tabs (and bottom bar + nav) directly into `FrameLayout` scaffold slots → overlapping chrome. | Docs updated to wrap in vertical `LinearLayout`. Consumer already wraps. Regression test documents wrapper expectation. |
| F03 | P1 | Docs / API discoverability | XML section title setter is `setHeaderTitle`, while list rows use `setHeadline`. Easy to call the wrong name when porting from Compose `title` / `headline`. | Clarified in `XML_APPLICATION_RECIPES.md`. No API rename (binary/source risk). |
| F04 | P2 | Compose/XML parity | Compose scaffold slots vs XML `*Container` + host `ScrollView`/`LinearLayout` glue. | Expected surface difference; document only. |
| F05 | P2 | Naming | XML setters (`setBarTitle`, `setSkText`, `setButtonEnabled`, `setBadgeText`) differ from Compose param names. | Documented intentional; do not rename in alpha. |
| F06 | P2 | Forms | Compose needs `ProvideSKFormController`; XML uses `SKTextFieldView.bind(runtime, form)`. Same controller, different wiring. | Already covered in recipes. |
| F07 | P2 | Icons | Default `SKNoOpIconProvider` → placeholder glyphs unless host supplies icons. | Host responsibility; not a missing widget. |
| F08 | P2 | Snackbar | Host must clear/hide snackbars; no queue / auto-dismiss API. | Host `visible` / `setSnackbarVisible`. |
| F09 | — | Internal APIs | No internal SKOne types required for A/B/C. | — |
| F10 | — | Missing widgets | No missing P0/P1 primitive found for these flows when alpha02 APIs are present in-tree. | No new widgets. |

## Notes

- Excessive custom code was mainly **XML layout glue** (ScrollView + LinearLayout wrappers) — expected for View hosts, amplified by FrameLayout slots (F02).
- No crash/API hole demonstrated inside published APIs beyond F01 (artifact absence).
- Do not invent widgets to reduce host ScrollView boilerplate.
