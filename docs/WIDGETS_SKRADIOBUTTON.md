# SKRadioButton

Single-option radio control — Compose `SKRadioButton` / XML `SKRadioButtonView`.

Prefer placing inside [SKRadioGroup](WIDGETS_SKRADIOGROUP.md) for exclusive selection.

```kotlin
SKRadioButton(
    value = "light",
    label = "Light",
    accessibility = SKAccessibilityConfig(testTag = "theme_light"),
)
```

Role defaults to `radio`. Selected state uses Compose `selected` + stateDescription.
