# SKOne Developer Playground

Official showcase for every SKOne component (Compose + XML).

**Version:** 1.3.1 — developer experience only (no new production widgets).

## Run

```bash
./gradlew :samples:skone-playground:installDebug
```

## Features

| Feature | Description |
|---------|-------------|
| Component catalog | Indexed entries for widgets, frameworks, samples, docs |
| Widget gallery | Visual grid of live SKOne widgets |
| Live property editor | Tweak props and preview instantly |
| Codegen | Generated Compose and XML snippets |
| Theme switcher | Light / Dark / System via `SKThemeMode` |
| Documentation | In-app articles + static `docs-site/` |
| Sample browser | Curated integration recipes |
| Search | Filters catalog by name, tags, description |
| Navigation | Compose Navigation destinations |

## Extending for new widgets

1. Add a `CatalogEntry` in `PlaygroundCatalog`.
2. Add gallery preview + property model + Compose/XML generators.
3. Link related docs / samples.

See [ADR 0012](adr/0012-developer-experience-playground.md).

## Docs site

Open [`docs-site/index.html`](../docs-site/index.html) in a browser, or:

```bash
cd docs-site && python3 -m http.server 8080
```
