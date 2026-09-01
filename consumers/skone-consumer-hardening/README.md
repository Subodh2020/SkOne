# SKOne Consumer Hardening

Completely **external** sample application. It is **not** included in the SKOne root `settings.gradle.kts` and does **not** depend on `project(":skone-*")`, `mavenLocal()`, or playground modules.

## Dependency policy

```kotlin
implementation(platform("com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02"))
implementation("com.thesubodhgupta.skone:skone-compose")
implementation("com.thesubodhgupta.skone:skone-xml")
```

Repositories: Google + Maven Central only (`FAIL_ON_PROJECT_REPOS`).

## Flows

| Flow | Compose | XML |
|------|---------|-----|
| A List + Search + Filter | `ui/ListSearchFilterScreen` | `xml/XmlListFilterActivity` |
| B Form + Validation | `ui/FormValidationScreen` | `xml/XmlFormActivity` |
| C App Shell + Navigation | `ui/ShellNavigationScreen` | `xml/XmlShellActivity` |

Host-owned state covers loading / empty / error+retry / disabled controls / selection / dialogs / bottom sheet / snackbar / navigation / a11y / testTags / theme.

## Build

```bash
cd consumers/skone-consumer-hardening
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

### Current Central status

As of this milestone, **`1.4.0-alpha02` is not on Maven Central** (HTTP 404). Latest published BOM is **`1.4.0-alpha01`** (Text/TextField flagship only). This app intentionally targets alpha02’s full widget surface; resolution fails until alpha02 is published.

Do **not** work around with `mavenLocal()` or `project()` — that would invalidate the consumer readiness bar.

## Friction log

See [CONSUMER_FRICTION.md](CONSUMER_FRICTION.md) and [CONSUMER_HARDENING_REPORT.md](CONSUMER_HARDENING_REPORT.md).
