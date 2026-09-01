# SKTooltip

Host-controlled tooltip — Compose `SKTooltip` / XML `SKTooltipView`.

```kotlin
SKTooltip(
    message = "Archive thread",
    visible = showTip,
    accessibility = SKAccessibilityConfig(testTag = "tip"),
)
```

Uses `SKAppearanceConfig.Tooltip`.

## Accessibility

- Not a live region (avoids noisy duplicate announcements)
- Prefer putting durable info on the host control’s content description
- Tooltip announces message only while visible

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Hide | early return | `View.GONE` |
| Positioning | host layout | host layout / optional popup by host |

**Deferred:** tooltip manager, hover orchestration, global queue, rich positioning engine.
