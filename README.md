# SKOne

**SKOne** is an open-source, enterprise-grade Android developer platform — modular, Kotlin-first, AI-ready, with Jetpack Compose and XML support.

Current release line: **1.1.0 (Design System)** — theme engine, tokens, and component contracts. **No production widgets yet.**

## Modules

| Artifact | Description |
|----------|-------------|
| `skone-bom` | Bill of Materials |
| `skone-common` | `SKResult`, `SKError`, logging SPI, annotations |
| `skone-plugin` | Plugin SPI and registry |
| `skone-theme` | Design tokens, size/shape/state systems, theme engine |
| `skone-core` | `SKOne` init, AI SPI, component contracts **and framework** |
| `skone-compose` | Compose theme bridge + component lifecycle helpers — **no widgets** |
| `skone-xml` | XML theme bridge + `SKXmlComponent` base — **no widgets** |
| `samples:skone-sample` | Design-system showcase |

## Quick start

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SKOne.initialize(SKOneConfig(plugins = listOf(/* … */)))
    }
}

// Compose
SKTheme(mode = SKThemeMode.System) {
    val primary = skTheme.tokens.colors.primary.toColor()
}

// XML / Views
SKThemeHelper.install(context, mode = SKThemeMode.System)
val theme = SKThemeHelper.require()
```

Local modules:

```kotlin
implementation(platform(project(":skone-bom")))
implementation(project(":skone-core"))
implementation(project(":skone-compose")) // or skone-xml
```

## Documentation

- [SDK API Guidelines](docs/SDK_API_GUIDELINES.md)
- [Design System](docs/DESIGN_SYSTEM.md)
- [Component Framework](docs/COMPONENT_FRAMEWORK.md)
- [Architecture diagrams](docs/architecture/DESIGN_SYSTEM_ARCHITECTURE.md)
- [Architecture Decision Records](docs/adr/)

## Build

```bash
./gradlew check
./gradlew :samples:skone-sample:assembleDebug
./gradlew publishToMavenLocal
```

## Requirements

- Android API 24+
- JDK 17
- Kotlin 2.0+

## License

Apache License 2.0 (planned for public release).
