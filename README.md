# SKOne

**SKOne** is an open-source, enterprise-grade Android developer platform — modular, Kotlin-first, AI-ready, with Jetpack Compose and XML support.

**Repository:** https://github.com/Subodh2020/SkOne  
**Release version:** **1.3.2-alpha05** (publish to Maven Central via GitHub Actions)

**1.3.2-alpha05** — Compose `SKTextField` IME Next/Previous moves actual UI focus via `FocusRequester`. No public API change. See [Release Notes](docs/RELEASE_NOTES.md).

Stable public APIs remain usable without `@OptIn`. Direct use of newly `@SKInternal` implementation types may need `@OptIn(SKInternal::class)`.

## Install from Maven Central

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.3.2-alpha05"))
    implementation("com.thesubodhgupta.skone:skone-compose")
}
```

Group: `com.thesubodhgupta.skone` · Kotlin packages: `io.skone.*`

See [Getting Started](docs/GETTING_STARTED.md#choosing-skone-dependencies) for Compose vs XML dependency choices and transitive modules.

## Maven Central

| | |
|---|---|
| **Group** | `com.thesubodhgupta.skone` |
| **Latest (Central)** | `1.3.2-alpha05` (published) |
| **Browse** | https://repo.maven.apache.org/maven2/com/thesubodhgupta/skone/ |
| **Portal** | https://central.sonatype.com/ |

**Published artifacts (`1.3.2-alpha05`):**

| Artifact | Typical use |
|----------|-------------|
| `skone-bom` | Version alignment (declare first) |
| `skone-compose` | **Recommended Compose entry** (`SKText`, `SKTextField`, `SKTheme`) |
| `skone-xml` | **Recommended XML/View entry** (`SKTextView`, `SKTextFieldView`, `SKThemeHelper`) |
| `skone-common` | Results, errors, logging (advanced / headless) |
| `skone-plugin` | Plugin SPI (advanced / extension) |
| `skone-theme` | Design tokens (advanced; usually transitive) |
| `skone-core` | SDK init, component framework (advanced; usually transitive) |
| `skone-ui` | Widget contracts (usually transitive) |
| `skone-forms` | Form controller & validation (advanced; transitive via Compose/XML) |

Most Compose apps only need **`skone-bom` + `skone-compose`**. Lower-level modules are published for advanced use and are pulled transitively when needed.

See [Publishing Guide](docs/PUBLISHING.md) for maintainer workflows.

## Repository modules

These modules are part of the SKOne monorepo. All library artifacts below are published to Maven Central at **`1.3.2-alpha05`**.

| Module | Description | Maven Central |
|--------|-------------|---------------|
| `skone-bom` | Bill of Materials | Yes |
| `skone-common` | Results, errors, logging | Yes |
| `skone-plugin` | Plugin SPI | Yes |
| `skone-theme` | Design tokens + theme engine | Yes |
| `skone-core` | Init, AI SPI, component framework | Yes |
| `skone-ui` | Shared widget contracts | Yes |
| `skone-forms` | Form controller & engines | Yes |
| `skone-compose` | Compose bridges + widgets | Yes |
| `skone-xml` | XML bridges + widgets | Yes |
| `samples:skone-sample` | Minimal integration sample | Not published |
| `samples:skone-playground` | Official developer showcase | Not published |
| `samples:skone-demo` | External Maven Central consumer | Not published |

## Quick commands

```bash
./gradlew check
./gradlew verifyPublishing
./gradlew publishToMavenLocal
./gradlew publishToLocalTestRepository
./gradlew :samples:skone-playground:installDebug
```

## Documentation

- [Getting Started](docs/GETTING_STARTED.md)
- [API Reference](https://subodh2020.github.io/SkOne/api/) (hosted Dokka HTML)
- [Accessibility](docs/ACCESSIBILITY.md) — capability matrix (Compose + XML)
- [Publishing](docs/PUBLISHING.md)
- [Playground](docs/PLAYGROUND.md)
- [SDK API Guidelines](docs/SDK_API_GUIDELINES.md)
- [Design System](docs/DESIGN_SYSTEM.md)
- [Component Framework](docs/COMPONENT_FRAMEWORK.md)
- [Form Framework](docs/FORM_FRAMEWORK.md)
- [Docs site](docs-site/index.html)
- [ADRs](docs/adr/)

## Roadmap

Future milestones (for example **1.4.0**, **1.5.0**, **1.6.0**) describe planned work in [ADRs](docs/adr/) and module documentation. They are **not released** unless explicitly listed under Maven Central above.

## License

Apache License 2.0 — see [LICENSE](LICENSE).
