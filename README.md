# SKOne

**SKOne** is an open-source, enterprise-grade Android developer platform — modular, Kotlin-first, AI-ready, with Jetpack Compose and XML support.

Current release line: **1.3.1** — Developer Experience playground (no new production widgets).

## Modules

| Artifact | Description |
|----------|-------------|
| `skone-bom` | Bill of Materials |
| `skone-common` | Results, errors, logging |
| `skone-plugin` | Plugin SPI |
| `skone-theme` | Design tokens + theme engine |
| `skone-core` | Init, AI SPI, component framework |
| `skone-ui` | Shared widget contracts (`SKText`, `SKTextField`) |
| `skone-forms` | Form controller, validation, formatters, masks, focus |
| `skone-compose` | Compose bridges + widgets |
| `skone-xml` | XML bridges + widgets |
| `samples:skone-sample` | Minimal integration sample |
| `samples:skone-playground` | **Official showcase** — catalog, gallery, editors, codegen |

## Developer playground (1.3.1)

```bash
./gradlew :samples:skone-playground:installDebug
```

- Component catalog + search
- Widget gallery
- Live property editor
- Generated Compose / XML
- Theme switcher (Light / Dark / System)
- In-app docs + sample browser
- Static docs site: [`docs-site/`](docs-site/)

See [Playground Guide](docs/PLAYGROUND.md) and [ADR 0012](docs/adr/0012-developer-experience-playground.md).

## Documentation

- [SDK API Guidelines](docs/SDK_API_GUIDELINES.md)
- [Design System](docs/DESIGN_SYSTEM.md)
- [Component Framework](docs/COMPONENT_FRAMEWORK.md)
- [Form Framework](docs/FORM_FRAMEWORK.md)
- [SKText](docs/WIDGETS_SKTEXT.md) · [SKTextField](docs/WIDGETS_SKTEXTFIELD.md)
- [Docs site](docs-site/index.html)
- [ADRs](docs/adr/)

## Build

```bash
./gradlew check
./gradlew :samples:skone-playground:assembleDebug
```

## License

Apache License 2.0 (planned for public release).
