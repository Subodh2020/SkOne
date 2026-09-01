# SKChip

Foundational selectable chip — Compose `SKChip` / XML `SKChipView`.

**Deferred:** filter / input / assist / suggestion chip variants.

```kotlin
SKChip(
    label = "Compose",
    selected = selected,
    onClick = { selected = !selected },
    leadingIcon = SKIconKey("skone.icon.tag"), // decorative unless CD set
)
```

Appearance: `SKAppearanceConfig.Chip` / `ChipSelected` by default.
