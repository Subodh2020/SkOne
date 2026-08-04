# SKText — Reference Widget

`SKText` / `SKTextView` is the first production SKOne widget and the **coding standard**
for all future widgets.

## Principles demonstrated

1. Token-driven visuals only (`SKAppearanceConfig` + `SKTheme`)
2. Shared contract in `skone-ui` (`SKTextComponent`)
3. Compose + XML dual surface
4. Component framework lifecycle, analytics, plugins
5. Accessibility defaults (content description, semantics, RTL)
6. Stable parameter order per API guidelines
7. No hardcoded colors, type sizes, or spacing

## Modules

| Artifact | API |
|----------|-----|
| `skone-ui` | `SKTextComponent`, `SKAnnotatedText`, overflow/align |
| `skone-compose` | `SKText` composable |
| `skone-xml` | `SKTextView` |

## Compose usage

```kotlin
SKTheme {
    val runtime = rememberSKComponentRuntime()
    CompositionLocalProvider(LocalSKComponentRuntime provides runtime) {
        SKText(
            text = "Hello SKOne",
            appearance = SKAppearanceConfig.Text,
            accessibility = SKAccessibilityConfig(contentDescription = "Greeting"),
        )
        SKText(
            annotated = SKAnnotatedText(
                text = "Bold and colored",
                spans = listOf(
                    SKTextSpan(0, 4, listOf(SKSpanStyle.Bold)),
                    SKTextSpan(9, 16, listOf(SKSpanStyle.ColorRole(SKColorRole.Primary))),
                ),
            ),
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = SKTypographyRole.TitleMedium,
            ),
        )
    }
}
```

## XML usage

```xml
<io.skone.xml.widget.SKTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:skText="Hello SKOne"
    app:skTypographyRole="bodyLarge"
    app:skContentColorRole="onSurface"
    app:skMaxLines="2"
    app:skOverflow="ellipsis" />
```

Bind runtime in code:

```kotlin
textView.bind(SKComponentRuntime.create())
```

## Parameter order (Compose)

1. `modifier`
2. `text` / `annotated` (value)
3. `onClick` (callbacks)
4. — (content N/A for text)
5. `appearance`
6. behavior fields: `overflow`, `maxLines`, `softWrap`, `textAlign`
7. validation N/A
8. `accessibility`
9. `analytics`
10. `ai`

## Testing

- Unit: `SKTextComponent`, annotated model
- Compose UI + accessibility: `androidTest`
- XML: Robolectric unit tests
- Screenshot: Paparazzi (`SKTextScreenshotTest`)

## Related

- [ADR 0009](adr/0009-sktext-reference-widget.md)
- [Component Framework](COMPONENT_FRAMEWORK.md)
- [Design System](DESIGN_SYSTEM.md)
