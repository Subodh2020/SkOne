# SKOne Developer Playground

Official showcase for SKOne components (Compose + XML) plus **Application Examples**.

**Version:** `1.4.0-alpha01` — consumer-readiness milestone.

Published Maven Central still ships the flagship Text/TextField surface; Playground uses **in-tree** modules for the full widget kit.

## Run

```bash
./gradlew :samples:skone-playground:installDebug
```

## Features

| Feature | Description |
|---------|-------------|
| **Application Examples** | List + Search + Filter, Form + Validation, App Shell + Navigation |
| Component catalog | Widgets, frameworks, samples, docs |
| Widget gallery | Live SKOne widgets |
| Sample browser | Application Examples first, then smaller recipes (incl. XML scaffold recipe) |
| Theme switcher | Light / Dark / System |
| Documentation | In-app + `docs-site/` + [CONSUMER_GUIDE.md](CONSUMER_GUIDE.md) |

## Guides

- [Consumer Guide](CONSUMER_GUIDE.md) — published vs in-tree
- [Screen composition](architecture/SCREEN_COMPOSITION.md)
- [XML application recipes](architecture/XML_APPLICATION_RECIPES.md)
- [Alpha release checklist](release/ALPHA_RELEASE_CHECKLIST.md)

## Extending

1. Prefer composing Application Examples over new one-off widgets.
2. For a new widget: catalog entry, gallery, editors, docs — and mark **in-tree** until published.
3. See [ADR 0012](adr/0012-developer-experience-playground.md).

## Docs site

Open [`docs-site/index.html`](../docs-site/index.html) or:

```bash
cd docs-site && python3 -m http.server 8080
```
