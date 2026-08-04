# SKOne

**SKOne** is an open-source, enterprise-grade Android developer platform — modular, Kotlin-first, AI-ready, with Jetpack Compose and XML support.

Current release line: **1.3.2-alpha01** — Production Maven Central publishing infrastructure (local/CI ready; Central upload opt-in).

## Maven coordinates

```kotlin
implementation(platform("com.thesubodhgupta.skone:skone-bom:1.3.2-alpha01"))
implementation("com.thesubodhgupta.skone:skone-compose:1.3.2-alpha01")
```

Group: `com.thesubodhgupta.skone` · Packages: `io.skone.*`  
See [Publishing Guide](docs/PUBLISHING.md).

## Modules

| Artifact | Description |
|----------|-------------|
| `skone-bom` | Bill of Materials |
| `skone-common` | Results, errors, logging |
| `skone-plugin` | Plugin SPI |
| `skone-theme` | Design tokens + theme engine |
| `skone-core` | Init, AI SPI, component framework |
| `skone-ui` | Shared widget contracts |
| `skone-forms` | Form controller & engines |
| `skone-compose` | Compose bridges + widgets |
| `skone-xml` | XML bridges + widgets |
| `samples:skone-sample` | Minimal integration sample |
| `samples:skone-playground` | Official developer showcase |

## Quick commands

```bash
./gradlew check
./gradlew verifyPublishing
./gradlew publishToMavenLocal
./gradlew publishToLocalTestRepository
./gradlew :samples:skone-playground:installDebug
```

## Documentation

- [Publishing](docs/PUBLISHING.md)
- [Playground](docs/PLAYGROUND.md)
- [SDK API Guidelines](docs/SDK_API_GUIDELINES.md)
- [Design System](docs/DESIGN_SYSTEM.md)
- [Component Framework](docs/COMPONENT_FRAMEWORK.md)
- [Form Framework](docs/FORM_FRAMEWORK.md)
- [Docs site](docs-site/index.html)
- [ADRs](docs/adr/)

## License

Apache License 2.0 — see [LICENSE](LICENSE).
