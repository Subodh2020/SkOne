# SKOne

**SKOne** is an open-source, enterprise-grade Android developer platform — modular, Kotlin-first, AI-ready, with Jetpack Compose and XML support.

**Repository:** https://github.com/Subodh2020/SkOne  
**Release version:** **1.3.2-alpha03** (publish to Maven Central via GitHub Actions)

**1.3.2-alpha03** — Improved SKTextField Compose UX with integrated floating-label outlined field.

## Install from Maven Central

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.3.2-alpha03"))
    implementation("com.thesubodhgupta.skone:skone-compose:1.3.2-alpha03")
}
```

Group: `com.thesubodhgupta.skone` · Kotlin packages: `io.skone.*`

> Maven Central may still list `1.3.2-alpha02` until the publish workflow completes for `1.3.2-alpha03`.

## Maven Central

| | |
|---|---|
| **Group** | `com.thesubodhgupta.skone` |
| **Latest (release line)** | `1.3.2-alpha03` |
| **Browse** | https://repo.maven.apache.org/maven2/com/thesubodhgupta/skone/ |
| **Portal** | https://central.sonatype.com/ |

**Target artifacts (`1.3.2-alpha03`):**

- `com.thesubodhgupta.skone:skone-bom:1.3.2-alpha03`
- `com.thesubodhgupta.skone:skone-common:1.3.2-alpha03`
- `com.thesubodhgupta.skone:skone-compose:1.3.2-alpha03`

Additional library modules exist in this repository and are configured for publishing, but are **not yet available** on Maven Central. Use the published coordinates above until further modules are released.

See [Publishing Guide](docs/PUBLISHING.md) for maintainer workflows.

## Repository modules

These modules are part of the SKOne monorepo. Only the Maven Central artifacts listed above are currently published for external consumption.

| Module | Description | On Maven Central |
|--------|-------------|------------------|
| `skone-bom` | Bill of Materials | Yes (`1.3.2-alpha03`) |
| `skone-common` | Results, errors, logging | Yes (`1.3.2-alpha03`) |
| `skone-compose` | Compose bridges + widgets | Yes (`1.3.2-alpha03`) |
| `skone-plugin` | Plugin SPI | Repository only |
| `skone-theme` | Design tokens + theme engine | Repository only |
| `skone-core` | Init, AI SPI, component framework | Repository only |
| `skone-ui` | Shared widget contracts | Repository only |
| `skone-forms` | Form controller & engines | Repository only |
| `skone-xml` | XML bridges + widgets | Repository only |
| `samples:skone-sample` | Minimal integration sample | Not published |
| `samples:skone-playground` | Official developer showcase | Not published |

## Quick commands

```bash
./gradlew check
./gradlew verifyPublishing
./gradlew publishToMavenLocal
./gradlew publishToLocalTestRepository
./gradlew :samples:skone-playground:installDebug
```

## Documentation

- [API Reference](https://subodh2020.github.io/SkOne/api/) (hosted Dokka HTML)
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
