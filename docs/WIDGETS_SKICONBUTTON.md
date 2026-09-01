# SKIconButton

Icon-only action — Compose `SKIconButton` / XML `SKIconButtonView`.

**Requires** a meaningful description via `accessibility.contentDescription` or `SKIconKey.contentDescription`.
Never announces the raw icon key.

```kotlin
SKIconButton(
    icon = SKIconKey("skone.icon.close", contentDescription = "Close"),
    onClick = { /* … */ },
    accessibility = SKAccessibilityConfig(testTag = "close"),
)
```

Uses `SKAppearanceConfig.IconButton`. Distinct from `SKButton` (no text label).
