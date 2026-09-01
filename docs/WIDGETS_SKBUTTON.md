# SKButton

Primary action widget for SKOne — Compose `SKButton` and XML `SKButtonView`.

**Status:** In-tree development after published `1.4.0-alpha01` (not a Maven Central release claim).

## Install

```kotlin
implementation(platform("com.thesubodhgupta.skone:skone-bom:<version>"))
implementation("com.thesubodhgupta.skone:skone-compose") // and/or skone-xml
```

## Compose

```kotlin
SKTheme {
    SKButton(
        text = "Continue",
        onClick = { /* … */ },
        appearance = SKAppearanceConfig.Button, // or ButtonTonal / ButtonOutlined / ButtonText
        enabled = true,
        loading = false,
        leadingIcon = SKIconKey("skone.icon.arrow"), // decorative unless contentDescription set
        accessibility = SKAccessibilityConfig(testTag = "continue"),
    )
}
```

### Parameter order

`modifier → enabled → loading → onClick → text → leadingIcon → appearance → accessibility → analytics → ai`

### Variants

| Preset | Look |
|--------|------|
| `SKAppearanceConfig.Button` | Filled primary |
| `SKAppearanceConfig.ButtonTonal` | Soft filled (primary container) |
| `SKAppearanceConfig.ButtonOutlined` | Outline |
| `SKAppearanceConfig.ButtonText` | Text / borderless |

### Behavior

- **Disabled** (`enabled = false`): reduced alpha, no clicks, `Disabled` semantics.
- **Loading** (`loading = true`): shows a compact loading glyph, keeps label when non-blank, blocks clicks, `stateDescription` includes `"Loading"`.
- Default accessibility **role** is `"button"` when unset.
- Leading icons follow the same decorative rule as `SKTextField` IconSlot.

## XML

```xml
<io.skone.xml.widget.SKButtonView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:skText="Continue"
    app:skButtonVariant="filled"
    app:skEnabled="true"
    app:skLoading="false"
    app:skTestTag="continue" />
```

```kotlin
button.bind(runtime)
button.setOnSkClickListener { /* … */ }
button.setAppearance(SKAppearanceConfig.ButtonOutlined)
```

## Shared contract

`io.skone.ui.button.SKButtonComponent` — no UI; used by both surfaces.

## Testing

- Unit: `SKButtonComponentTest`
- Compose androidTest: `SKButtonComposeTest`
- XML Robolectric: `SKButtonViewTest`

## Related

- [Accessibility](ACCESSIBILITY.md)
- [SDK API Guidelines](SDK_API_GUIDELINES.md)
- [Design System](DESIGN_SYSTEM.md)
