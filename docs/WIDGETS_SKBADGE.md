# SKBadge

Compact status / count badge — Compose `SKBadge` / XML `SKBadgeView`.

```kotlin
SKBadge(text = "3", accessibility = SKAccessibilityConfig(testTag = "badge"))
SKBadge(dot = true) // decorative by default
SKBadge(text = "9+", visible = false)
```

Uses `SKAppearanceConfig.Badge`.

## Accessibility

- Count/text badges announce the text (or explicit `contentDescription`)
- Dot-only badges are **silent** unless `contentDescription` is provided
- Hidden badges (`visible = false`) are not composed / `GONE`

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Hide | early return when `!visible` | `View.GONE` |
| Text attr | `text` | `skBadgeText` / `setBadgeText` |

**Deferred:** notification queue, overflow `"99+"`, badge anchoring/layout helpers.
