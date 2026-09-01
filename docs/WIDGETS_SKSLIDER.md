# SKSlider

Continuous or stepped value control — Compose `SKSlider` / XML `SKSliderView`.

```kotlin
SKSlider(
    value = volume,
    onValueChange = { volume = it },
    valueRange = 0f..1f,
    steps = 4, // 0 = continuous
    enabled = true,
    accessibility = SKAccessibilityConfig(testTag = "volume", contentDescription = "Volume"),
)
```

Exposes progress/range semantics + current value. **Deferred:** range slider, dual-thumb, vertical, custom thumb.
