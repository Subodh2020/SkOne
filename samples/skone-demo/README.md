# SKOne Demo

This sample demonstrates consuming SKOne from **Maven Central** as an external Android application.

It intentionally does **not** depend on SKOne source modules in this repository. There are no `project(...)` dependencies, no `mavenLocal()` repository, and no copied SKOne source code.

## Current version

`1.3.2-alpha02` (latest published on Maven Central)

> The SKOne monorepo release line is `1.3.2-alpha03`. This demo stays on `alpha02` until `alpha03` is published to Maven Central, then it will be updated in a follow-up change.

## Maven coordinates

**Group:** `com.thesubodhgupta.skone`

## Installation

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.3.2-alpha02"))
    implementation("com.thesubodhgupta.skone:skone-compose:1.3.2-alpha02")
}
```

`skone-compose` pulls transitive SKOne modules (for example `skone-core`, `skone-theme`, `skone-ui`, `skone-forms`) from Maven Central as required by the published POM.

## Run from the SKOne monorepo (Android Studio)

Open the **repository root** (`SkOne/`) in Android Studio, sync Gradle, then select:

**`skone.samples.skone-demo`**

Or from the repo root:

```bash
./gradlew :samples:skone-demo:installDebug
./gradlew :samples:skone-demo:assembleDebug
```

Remove any broken **`app`** run configuration (red X) from **Run → Edit Configurations** — that comes from opening the nested standalone project earlier.

This module still resolves SKOne from **Maven Central only** — it is not wired to `:skone-*` project modules.

## Standalone Gradle project (optional)

You can also open `samples/skone-demo/` as its own Gradle project.

Copy `local.properties.example` → `local.properties` and set `sdk.dir` if Android Studio does not create it.

```bash
cd samples/skone-demo
cp local.properties.example local.properties   # edit sdk.dir
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

## Demonstrated APIs

The demo screen uses public APIs from the published Maven Central artifacts:

| API | Module (via Maven) | Usage |
|-----|-------------------|--------|
| `SKTheme` | `skone-compose` | Provides SKOne theme tokens to Compose |
| `skTheme` | `skone-compose` | Reads active theme in composables |
| `toColor()` | `skone-compose` | Converts SKOne color tokens for Compose |
| `SKText` | `skone-compose` | Renders themed text widget |
| `SKTextField` | `skone-compose` | Outlined field with floating label, hint, and validation |
| `SKAppearanceConfig` | transitive (`skone-core`) | Text/field appearance configuration |
| `SKAccessibilityConfig` | transitive (`skone-core`) | Accessibility metadata for `SKText` |
| `SKTypographyRole` / `SKColorRole` | transitive (`skone-theme`) | Design token roles |
| `SKResult` | transitive (`skone-common`) | Result type used in unit test + status line |

### SKTextField example (published API)

Floating label, hint, supporting text, and validation using the existing public API:

```kotlin
var email by remember { mutableStateOf("") }

SKTextField(
    modifier = Modifier.fillMaxWidth(),
    value = email,
    onValueChange = { email = it },
    fieldId = "demo_email",
    label = "Email",
    hint = "Enter your email",
    supportingText = "Enter your email address",
    required = true,
    keyboardType = SKKeyboardType.Email,
    imeAction = SKImeAction.Done,
    accessibility = SKAccessibilityConfig(
        contentDescription = "Email address",
        testTag = "demo_email",
    ),
)
```

**Label / hint behavior:**
- Empty + unfocused → label inside the outlined field
- Focused + empty → label floats; hint appears inside the field
- Value entered → label stays floated; hint hidden

Imports: `io.skone.compose.widget.SKTextField`, `io.skone.ui.field.SKImeAction`, `io.skone.ui.field.SKKeyboardType`.

## Purpose

Use this sample to validate that external developers can add SKOne from Maven Central and compile against the published public API without cloning the SKOne monorepo.
