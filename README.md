# SKOne

**SKOne** is an open-source, enterprise-grade Android developer platform — modular, Kotlin-first, AI-ready, with Jetpack Compose and XML support.

**Repository:** https://github.com/Subodh2020/SkOne  
**Release version:** **1.4.0-alpha02** (alpha pre-release — full widget kit)

**1.4.0-alpha02** — Additive widget surface (Button through BottomSheet/SegmentedButton), Application Examples quality bar, Consumer Hardening Maven proof. See [Release Notes](docs/RELEASE_NOTES.md).

Stable public APIs remain usable without `@OptIn`. Direct use of `@SKInternal` implementation types may need `@OptIn(SKInternal::class)`.

## Install from Maven Central

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02"))
    implementation("com.thesubodhgupta.skone:skone-compose")
}
```

Group: `com.thesubodhgupta.skone` · Kotlin packages: `io.skone.*`

See [Getting Started](docs/GETTING_STARTED.md#choosing-skone-dependencies) for Compose vs XML dependency choices and transitive modules.

## Maven Central

| | |
|---|---|
| **Group** | `com.thesubodhgupta.skone` |
| **Latest (Central)** | `1.4.0-alpha02` |
| **Browse** | https://repo.maven.apache.org/maven2/com/thesubodhgupta/skone/ |
| **Portal** | https://central.sonatype.com/ |

**Published artifacts (`1.4.0-alpha02`):**

| Artifact | Typical use |
|----------|-------------|
| `skone-bom` | Version alignment (declare first) |
| `skone-compose` | **Recommended Compose entry** (theme + full widget kit) |
| `skone-xml` | **Recommended XML/View entry** (`SKThemeHelper` + `SK*View`) |
| `skone-common` | Results, errors, logging (advanced / headless) |
| `skone-plugin` | Plugin SPI (advanced / extension) |
| `skone-theme` | Design tokens (advanced; usually transitive) |
| `skone-core` | SDK init, component framework (advanced; usually transitive) |
| `skone-ui` | Widget contracts (usually transitive) |
| `skone-forms` | Form controller & validation (advanced; transitive via Compose/XML) |

Most Compose apps only need **`skone-bom` + `skone-compose`**. Lower-level modules are published for advanced use and are pulled transitively when needed.

See [Publishing Guide](docs/PUBLISHING.md) for maintainer workflows.

## Repository modules

These modules are part of the SKOne monorepo. All library artifacts below are published to Maven Central at **`1.4.0-alpha02`**.

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
- [Consumer Guide](docs/CONSUMER_GUIDE.md) — published vs in-tree vs next alpha
- [API Reference](https://subodh2020.github.io/SkOne/api/) (hosted Dokka HTML)
- [Accessibility](docs/ACCESSIBILITY.md) — capability matrix (Compose + XML)
- [SKButton](docs/WIDGETS_SKBUTTON.md) — primary action widget (in-tree development)
- [Publishing](docs/PUBLISHING.md)
- [Playground](docs/PLAYGROUND.md)
- [Screen composition](docs/architecture/SCREEN_COMPOSITION.md) — how to build real app surfaces
- [XML application recipes](docs/architecture/XML_APPLICATION_RECIPES.md)
- [Alpha release checklist](docs/release/ALPHA_RELEASE_CHECKLIST.md)
- [Alpha02 release plan](docs/release/ALPHA02_RELEASE_PLAN.md)
- [SDK API Guidelines](docs/SDK_API_GUIDELINES.md)
- [Design System](docs/DESIGN_SYSTEM.md)
- [Component Framework](docs/COMPONENT_FRAMEWORK.md)
- [Form Framework](docs/FORM_FRAMEWORK.md)
- [Docs site](docs-site/index.html)
- [ADRs](docs/adr/)

## Roadmap

Future milestones (for example **1.4.0**, **1.5.0**, **1.6.0**) describe planned work in [ADRs](docs/adr/) and module documentation. They are **not released** on Maven Central unless explicitly listed as published above.

## License

Apache License 2.0 — see [LICENSE](LICENSE).
