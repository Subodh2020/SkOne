# SKOne Consumer Guide

How an **external Android developer** installs and uses SKOne.

| Layer | Meaning |
|-------|---------|
| **A. Published `1.4.0-alpha02`** | Full widget kit + Application Example quality bar on Maven Central |
| **B. Prior `1.4.0-alpha01`** | Flagship Text/TextField surface (still resolvable; superseded) |
| **C. Consumer Hardening** | Standalone Maven-only proof at `consumers/skone-consumer-hardening/` (`skone-bom:1.4.0-alpha02`) |

For friction log / hardening report, see [`consumers/skone-consumer-hardening/`](../consumers/skone-consumer-hardening/).

---

## 1. What is SKOne?

SKOne is an Android design-system SDK (Compose + XML) with:

- Token-driven theming (`skone-theme` / `SKTheme`)
- Shared widget contracts (`skone-ui`)
- Host-owned state (widgets stay presentational)
- Optional forms (`SKFormController`)
- Accessibility configuration (`SKAccessibilityConfig`)

Kotlin packages: `io.skone.*`  
Maven group: `com.thesubodhgupta.skone`

---

## 2. Which dependency should I use?

**Compose app (recommended):**

```kotlin
repositories { mavenCentral() }

dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02"))
    implementation("com.thesubodhgupta.skone:skone-compose")
}
```

**XML / View app:**

```kotlin
dependencies {
    implementation(platform("com.thesubodhgupta.skone:skone-bom:1.4.0-alpha02"))
    implementation("com.thesubodhgupta.skone:skone-xml")
}
```

Also declare your own AndroidX Compose BOM when using Compose. The SKOne BOM does **not** manage Compose/AndroidX versions.

---

## 3. How does BOM usage work?

`skone-bom` aligns versions for:

`skone-common`, `skone-plugin`, `skone-theme`, `skone-core`, `skone-ui`, `skone-forms`, `skone-compose`, `skone-xml`.

Declare the BOM once, then add modules **without** repeating the version (preferred). Transitive modules resolve automatically for typical apps.

Browse artifacts: https://repo.maven.apache.org/maven2/com/thesubodhgupta/skone/

---

## 4. Compose quick start

```kotlin
SKTheme(mode = SKThemeMode.System) {
    SKScaffold(
        topBar = { SKTopAppBar(title = "Inbox") },
    ) {
        SKText(text = "Hello SKOne")
        SKTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            rules = listOf(SKRequiredRule(), SKEmailRule()),
        )
        SKButton(text = "Save", onClick = { /* host */ })
    }
}
```

**Published Compose surface (`1.4.0-alpha02`):** theme/forms/runtime plus the full widget kit used by Application Examples (Button, Scaffold, SearchBar, EmptyState, BottomSheet, NavigationBar, Dialog, Snackbar, …).

No consumer `@OptIn` is required for public Compose widgets.

---

## 5. XML quick start

```kotlin
SKThemeHelper.install(SKThemes.Light)
val field = SKTextFieldView(context).apply {
    setLabel("Email")
    setRequired(true)
}
```

**Host theme:** XML activities that extend `AppCompatActivity` must use a `Theme.AppCompat` (or descendant) activity theme. Using a platform `android:Theme.Material.*` theme causes:

`IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.`

**Published XML surface (`1.4.0-alpha02`):** `SKThemeHelper`, flagship Text/TextField views, plus matching `SK*View` / hosts for the widget kit. See [XML application recipes](architecture/XML_APPLICATION_RECIPES.md).

---

## 6. Theme / appearance

- Compose: wrap UI in `SKTheme`; appearance presets live on `SKAppearanceConfig` (`Text`, `TextField`, `Button`, `Scaffold`, …).
- XML: `SKThemeHelper.install(...)` early in `Application` / `Activity`.
- Prefer presets + token roles over hardcoded colors/sizes.

---

## 7. Accessibility expectations

- Pass `SKAccessibilityConfig` (`testTag`, `contentDescription`, `role`, …) on interactive widgets.
- Icon-only Compose controls **require** an explicit content description on the icon key / a11y config.
- Host owns focus order for complex screens; forms integrate with `SKFormController` focus where used.

See [ACCESSIBILITY.md](ACCESSIBILITY.md).

---

## 8. Application Examples (quality bar)

Compose proof: Playground Application Examples (`samples/skone-playground/.../app/`).  
XML recipes: [XML_APPLICATION_RECIPES.md](architecture/XML_APPLICATION_RECIPES.md).  
External Maven proof: `consumers/skone-consumer-hardening/`.

| Flow | Intent |
|------|--------|
| A List + Search + Filter | Tabs, search, sheet filters, loading/empty/error, snackbar |
| B Form + Validation | Fields, toggles, submit loading, discard dialog |
| C App Shell + Navigation | TopAppBar, nav bar, menu, FAB, destinations |

Host owns state. No SKOne navigation/overlay/form “manager” frameworks.

---

## 9. Versioning

| Version | Status |
|---------|--------|
| `1.4.0-alpha02` | Current published target (full widget surface) |
| `1.4.0-alpha01` | Prior published flagship Text/TextField alpha |

See [PUBLISHING.md](PUBLISHING.md), [ALPHA02_RELEASE_PLAN.md](release/ALPHA02_RELEASE_PLAN.md), and [RELEASE_NOTES.md](RELEASE_NOTES.md).

---

## 10. Samples

[`samples/skone-demo`](../samples/skone-demo/) uses **only** Maven Central (`skone-bom` + `skone-compose` + `skone-xml`). It does **not** use `project(":skone-*")`, `mavenLocal()`, or playground helpers.

[`consumers/skone-consumer-hardening/`](../consumers/skone-consumer-hardening/) is the external Maven-only Application Examples consumer.
