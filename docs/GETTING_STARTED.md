# Getting Started

Get from zero to a working SKOne Compose screen in a few minutes.

This guide uses the published Maven Central release **`1.4.0-alpha02`**.

For the full consumer story, start with **[CONSUMER_GUIDE.md](CONSUMER_GUIDE.md)**.

## Published surface (Maven Central `1.4.0-alpha02`)

| Layer | Available |
|-------|-----------|
| Compose | `SKTheme`, forms/runtime, full widget kit (Text/TextField, Button, Scaffold, SearchBar, EmptyState, BottomSheet, Navigation, Dialog, Snackbar, …) |
| XML | `SKThemeHelper` + matching `SK*View` / hosts |

See Application Examples and [XML_APPLICATION_RECIPES.md](architecture/XML_APPLICATION_RECIPES.md).

## 1. Requirements

SKOne `1.4.0-alpha02` is built and tested with the toolchain below (from this repository’s Gradle configuration):

| Requirement | Version used by SKOne |
|-------------|----------------------|
| **Android `minSdk`** | 24 |
| **Android `compileSdk`** | 35 |
| **Kotlin** | 2.0.21 |
| **Jetpack Compose** | Compose BOM `2024.12.01` |
| **Java** | 17 |

You need:

- An **Android application** module with **Jetpack Compose** enabled (`buildFeatures { compose = true }`).
- The **Kotlin Compose compiler plugin** (`org.jetbrains.kotlin.plugin.compose`, same Kotlin version as above).
- `androidx.activity:activity-compose` (or equivalent) to call `setContent { }` from an `Activity`.

SKOne Compose widgets do **not** require Material3 in your app, but `skone-compose` may pull Compose libraries transitively.

## 2. Installation

Add Maven Central and the SKOne BOM, then depend on `skone-compose`:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02"))
    implementation("com.thesubodhgupta.skone:skone-compose")
}
```

### Why use the BOM?

`skone-bom` pins compatible versions of all eight SKOne library modules. Declare the BOM once, then add module dependencies **without** repeating version numbers. Gradle resolves matching `1.4.0-alpha01` artifacts for every SKOne module on your classpath.

The BOM is defined in `skone-bom/build.gradle.kts` and constrains: `skone-common`, `skone-plugin`, `skone-theme`, `skone-core`, `skone-ui`, `skone-forms`, `skone-compose`, and `skone-xml`.

**The SKOne BOM does not manage Jetpack Compose or other AndroidX libraries.** Your application must still declare the Android Compose BOM (or individual Compose libraries) separately.

**Group ID:** `com.thesubodhgupta.skone`  
**Kotlin packages:** `io.skone.*`

Browse published artifacts: https://repo.maven.apache.org/maven2/com/thesubodhgupta/skone/

## Choosing SKOne dependencies

### Normal Compose application (recommended)

```kotlin
dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02"))
    implementation("com.thesubodhgupta.skone:skone-compose")

    // Required by your app — not managed by the SKOne BOM
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose")
}
```

This is the minimal SKOne setup for `SKTheme`, `SKText`, `SKTextField`, and form integration in Compose.

### XML / View application

```kotlin
dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02"))
    implementation("com.thesubodhgupta.skone:skone-xml")
}
```

Use `skone-xml` for `SKTextView`, `SKTextFieldView`, and `SKThemeHelper`. Install the theme with `SKThemeHelper.install(...)` in your `Application` or `Activity`.

If the host Activity extends `AppCompatActivity`, the activity theme must be `Theme.AppCompat` (or a descendant). A platform `android:Theme.Material.*` theme will crash at `setContentView`.

Do **not** add `skone-compose` unless you also use Compose in the same app.

### What is transitive?

When you depend on `skone-compose`, Gradle also resolves these SKOne modules (via `api` dependencies in the published POMs):

| Transitive module | Brought in by |
|-------------------|---------------|
| `skone-theme` | `skone-compose` |
| `skone-core` | `skone-compose` |
| `skone-ui` | `skone-compose` |
| `skone-forms` | `skone-compose` |
| `skone-common` | `skone-core`, `skone-forms` |
| `skone-plugin` | `skone-core` |

You do **not** need to declare these separately for a typical Compose app.

When you depend on `skone-xml`, you get `skone-theme`, `skone-core`, `skone-ui`, and (through `skone-ui`) `skone-forms`, `skone-common`, and `skone-plugin`. You do **not** get `skone-compose`.

### Direct module consumption

| Module | Direct app use? | Notes |
|--------|-----------------|-------|
| `skone-bom` | **Yes** | Always declare first for version alignment |
| `skone-compose` | **Yes** | Recommended Compose entry point |
| `skone-xml` | **Yes** | Recommended XML/View entry point |
| `skone-common` | Advanced | Headless utilities (`SKResult`, `SKError`) without UI |
| `skone-plugin` | Extension | Implement `SKPlugin` and register via `SKOne` |
| `skone-theme` | Advanced | Custom theme providers; usually transitive |
| `skone-core` | Advanced | `SKOne.initialize`, component framework |
| `skone-ui` | Rare | Widget contracts; usually transitive |
| `skone-forms` | Advanced | `SKFormController` without Compose/XML layer |

### Verified external consumer

The [`samples/skone-demo/`](../samples/skone-demo/) app consumes `skone-bom` + `skone-compose` from Maven Central only (no `project(...)` or `mavenLocal()`).

## 3. SKOne runtime and theme setup

SKOne Compose widgets resolve design tokens through **`SKTheme`** and optionally connect to the component framework through **`SKComponentRuntime`**. These are separate from **`SKOne.initialize()`**, which configures process-wide plugins, logging, and AI.

### Recommended Compose setup

Use this pattern at the root of your Compose UI (for example in `setContent { }`):

```kotlin
import io.skone.compose.component.ProvideSKComponentRuntime
import io.skone.compose.component.rememberSKComponentRuntime
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode

SKTheme(mode = SKThemeMode.System) {
    val runtime = rememberSKComponentRuntime()
    ProvideSKComponentRuntime(runtime) {
        // SKText, SKTextField, and other SKOne widgets
    }
}
```

| Piece | Public API | Role |
|-------|------------|------|
| **`SKTheme`** | `io.skone.compose.theme.SKTheme` | Provides design tokens (`skTheme`) to descendants. Resolves `SKThemeMode` (Light / Dark / System). Uses a built-in default provider — you do not need to configure one. |
| **`rememberSKComponentRuntime()`** | `io.skone.compose.component.rememberSKComponentRuntime` | Creates a remembered `SKComponentRuntime` for the composition. Call inside a `@Composable` (typically inside `SKTheme`). |
| **`ProvideSKComponentRuntime`** | `io.skone.compose.component.ProvideSKComponentRuntime` | Publishes the runtime to descendants via `LocalSKComponentRuntime`. Widgets pick it up automatically. |

Call `rememberSKComponentRuntime()` once per screen (or shared subtree) that should share the same runtime services. Pass optional `analytics`, `logger`, `icons`, or `plugins` parameters when you need custom behavior.

### What is required?

| Setup | Basic `SKText` / `SKTextField` | Recommended? | Notes |
|-------|-------------------------------|--------------|-------|
| **`SKTheme`** | Optional (defaults to light tokens) | **Yes** | Without `SKTheme`, `LocalSKTheme` falls back to `SKThemes.Light` — widgets render, but Light/Dark/System switching does not apply. |
| **`rememberSKComponentRuntime()` + `ProvideSKComponentRuntime`** | Optional | **Yes** | Without a runtime, widgets still render. Component attach/detach, analytics, focus, and validation registration through the runtime are skipped. |
| **`SKOne.initialize()`** | Not required | When using plugins, AI, or `SKOne.logger()` | `skone-demo` works without it. Required before `SKOne.plugins()`, `SKOne.logger()`, or `SKOne.aiComplete()`. |
| **`ProvideSKFormController`** | Not required | When using `SKFormController` | `SKTextField` auto-registers only when a form controller is in scope. Standalone fields and per-field `rules` work without it. |

### What happens when setup is omitted?

- **No `SKTheme`:** `SKText` and `SKTextField` still compose and draw using the default light token set (`LocalSKTheme` defaults to `SKThemes.Light`). Wrap with `SKTheme` when you need Light / Dark / System resolution.
- **No `ProvideSKComponentRuntime`:** Widgets still render. The underlying `SKTextComponent` / `SKTextFieldComponent` is not attached to a runtime, so framework events, analytics hooks, focus management, and runtime validation registration do not run. Icons fall back to a placeholder when `leadingIcon` / `trailingIcon` are set. Verified by `SKTextComposeTest` and `SKTextFieldComposeTest`, which render widgets with `SKTheme` only (no runtime).
- **No `SKOne.initialize()`:** Compose widgets are unaffected. Calling `SKOne.plugins()` or `SKOne.logger()` throws `IllegalStateException`. Component AI (`SKAIComponentConfig`) and form AI hooks call `SKOne.aiComplete()` and return a failure when AI is unavailable.

### Setup by feature

| Feature | Additional setup |
|---------|------------------|
| **Basic text and fields** | `SKTheme` + runtime (recommended) |
| **Light / dark / system theming** | `SKTheme(mode = …)` |
| **Custom analytics** | `rememberSKComponentRuntime(analytics = myHook)` |
| **Custom icons** | `rememberSKComponentRuntime(icons = myProvider)` |
| **Component plugins** | Pass `plugins` to `rememberSKComponentRuntime`, or use runtime `addPlugin()` |
| **Process-wide plugins / logging** | `SKOne.initialize(SKOneConfig(...))` in `Application.onCreate` |
| **AI on components or forms** | `SKOne.initialize` with `SKAIConfig` + enable `ai` on the widget or form |
| **Multi-field forms** | `SKFormController.create()` + `ProvideSKFormController` around fields |

### How samples differ

| Sample | `SKTheme` + runtime | `SKOne.initialize` | Forms |
|--------|--------------------|--------------------|-------|
| [`skone-demo`](../samples/skone-demo/) | Yes | No | No |
| [`skone-sample`](../samples/skone-sample/) | Yes | Yes (plugins) | Yes (`ProvideSKFormController`) |
| [`skone-playground`](../samples/skone-playground/) | Yes | Yes | Yes (gallery screens) |

`skone-demo` intentionally shows the **minimal external-consumer** setup. `skone-sample` and the Playground add `SKOne.initialize` and forms for broader SDK features.

## 4. First SKOne Screen

Wrap your UI in `SKTheme`, provide a component runtime, and render `SKText`:

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.component.ProvideSKComponentRuntime
import io.skone.compose.component.rememberSKComponentRuntime
import io.skone.compose.theme.SKTheme
import io.skone.compose.widget.SKText
import io.skone.theme.SKThemeMode
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SKTheme(mode = SKThemeMode.System) {
                val runtime = rememberSKComponentRuntime()
                ProvideSKComponentRuntime(runtime) {
                    Column(Modifier.padding(24.dp)) {
                        SKText(
                            text = "Hello, SKOne",
                            appearance = SKAppearanceConfig.Text.copy(
                                typographyRole = SKTypographyRole.HeadlineMedium,
                                contentColorRole = SKColorRole.OnBackground,
                            ),
                        )
                    }
                }
            }
        }
    }
}
```

This example uses the recommended setup from [§3](#3-skone-runtime-and-theme-setup): `SKTheme` for tokens, `rememberSKComponentRuntime()` + `ProvideSKComponentRuntime` for the component framework.

## 5. SKOne Theme

The public Compose theme API is `io.skone.compose.theme.SKTheme`:

```kotlin
import io.skone.compose.theme.SKTheme
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.theme.SKThemeMode

SKTheme(mode = SKThemeMode.System) {
    val primary = skTheme.tokens.colors.primary.toColor()
    // ...
}
```

- **`SKTheme`** — Composable that provides an `io.skone.theme.SKTheme` model to the hierarchy. Optional `provider` parameter accepts a custom `SKThemeProvider`; when omitted, a built-in default is used.
- **`skTheme`** — Read the active theme inside composables.
- **`SKThemeMode`** — `Light`, `Dark`, or `System`.
- **`toColor()`** — Convert SKOne color tokens to Compose `Color`.

Token roles (for example `SKColorRole`, `SKTypographyRole`) live in `io.skone.theme.tokens`.

## 6. SKText

`SKText` renders text using SKOne appearance tokens:

```kotlin
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.widget.SKText
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole

SKText(
    text = "Welcome",
    appearance = SKAppearanceConfig.Text.copy(
        typographyRole = SKTypographyRole.BodyLarge,
        contentColorRole = SKColorRole.OnBackground,
    ),
)
```

Common parameters:

| Parameter | Purpose |
|-----------|---------|
| `text` | Plain text to display |
| `appearance` | Typography and color roles (`SKAppearanceConfig`) |
| `modifier` | Compose layout modifier |
| `onClick` | Optional click handler |
| `accessibility` | Content description, heading, test tags |

An overload accepts `SKAnnotatedText` for styled spans. See the [API Reference](https://subodh2020.github.io/SkOne/api/skone-compose/skone-compose/io.skone.compose.widget/-s-k-text.html).

## 7. SKTextField

`SKTextField` is SKOne’s flagship Compose input:

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.skone.compose.widget.SKTextField
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType

var email by remember { mutableStateOf("") }

SKTextField(
    value = email,
    onValueChange = { email = it },
    fieldId = "email",
    label = "Email",
    hint = "name@company.com",
    supportingText = "We'll never share your email.",
    required = true,
    keyboardType = SKKeyboardType.Email,
    imeAction = SKImeAction.Done,
)
```

### Floating-label behavior (Compose)

In **`1.4.0-alpha01`**, the Compose `SKTextField` uses an integrated **floating-label outlined field**:

| State | Behavior |
|-------|----------|
| **Empty, unfocused** | Label is shown inside the field; hint is hidden |
| **Focused** | Label floats to the top border; hint is shown |
| **Value entered** | Label stays floated; hint is hidden |
| **Supporting text** | Rendered below the field when provided |
| **Required** | Label shows a required indicator (for example `Email *`) |

This improved floating-label UX applies to the **Compose** `SKTextField` in `1.4.0-alpha01`. The XML `SKTextFieldView` uses a separate layout and does not share this Compose implementation.

## 8. Forms and Validation

SKOne fields can validate with built-in rules and optionally register with `SKFormController`.

### Standalone validation

```kotlin
import io.skone.component.validation.SKValidationResult
import io.skone.forms.validation.SKEmailRule

val emailRule = remember { SKEmailRule() }
val validation = remember(email) { emailRule.validate(email) }

val supportingText = when {
    email.isBlank() -> "Enter your email address"
    validation is SKValidationResult.Invalid ->
        validation.errors.firstOrNull()?.message ?: "Enter a valid email address"
    else -> "Looks good"
}
```

Pass `supportingText` to `SKTextField` to surface the message.

### Form controller (concise)

`SKTextField` auto-registers with a form when `ProvideSKFormController` is present:

```kotlin
import androidx.compose.runtime.remember
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.widget.SKTextField
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKRequiredRule
import io.skone.forms.validation.SKEmailRule
import io.skone.ui.field.SKImeAction

val form = remember { SKFormController.create() }

ProvideSKFormController(form) {
    var email by remember { mutableStateOf("") }
    SKTextField(
        value = email,
        onValueChange = { email = it },
        fieldId = "email",
        label = "Email",
        required = true,
        rules = listOf(SKRequiredRule(), SKEmailRule()),
        imeAction = SKImeAction.Next,
    )
}
```

For controllers, schemas, focus chains, and submit flows, see [Form Framework](FORM_FRAMEWORK.md).

## 9. Playground

Explore widgets interactively in the **SKOne Playground** showcase app:

- **Docs:** https://subodh2020.github.io/SkOne/playground.html
- **Run locally:** `./gradlew :samples:skone-playground:installDebug`

The Playground provides a catalog, live property editor, theme switcher, and in-app samples. Use it to try APIs beyond this short guide.

## 10. API Reference

Browse generated Kotlin API documentation (Dokka HTML):

**https://subodh2020.github.io/SkOne/api/**

The API Reference lists packages, types, and public members for every SKOne library module. Start with [skone-compose](https://subodh2020.github.io/SkOne/api/skone-compose/index.html) for Compose widgets.

See also [API Reference guide](API_REFERENCE.md) for local generation commands.

## 11. Samples

| Sample | Purpose |
|--------|---------|
| [`samples/skone-demo/`](../samples/skone-demo/) | External Maven Central consumer (`1.4.0-alpha01`) — no `project(...)` dependencies |
| [`samples/skone-playground/`](../samples/skone-playground/) | Full interactive developer showcase (monorepo project deps) |
| [`samples/skone-sample/`](../samples/skone-sample/) | Minimal integration sample (monorepo project deps) |

The demo app mirrors the patterns in this guide: `SKTheme` + component runtime, `SKText`, `SKTextField`, and validation with `SKEmailRule`.

## 12. Next Steps

| Topic | Where to go |
|-------|-------------|
| **Components** | [Component Framework](COMPONENT_FRAMEWORK.md) · [docs-site/components](https://subodh2020.github.io/SkOne/components.html) |
| **Forms** | [Form Framework](FORM_FRAMEWORK.md) · [docs-site/forms](https://subodh2020.github.io/SkOne/forms.html) |
| **Design tokens** | [Design System](DESIGN_SYSTEM.md) |
| **Playground** | [Playground guide](PLAYGROUND.md) · https://subodh2020.github.io/SkOne/playground.html |
| **API Reference** | https://subodh2020.github.io/SkOne/api/ |
| **Widgets** | [SKText](WIDGETS_SKTEXT.md) · [SKTextField](WIDGETS_SKTEXTFIELD.md) |
| **API design** | [SDK API Guidelines](SDK_API_GUIDELINES.md) |
