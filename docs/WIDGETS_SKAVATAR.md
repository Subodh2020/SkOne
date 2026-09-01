# SKAvatar

Identity avatar — Compose `SKAvatar` / XML `SKAvatarView`.

Host supplies image content; initials are the deterministic fallback. **No** Coil/Glide/network loader.

```kotlin
SKAvatar(
    initials = "SK",
    accessibility = SKAccessibilityConfig(contentDescription = "Subodh"),
)
SKAvatar(
    initials = "SK",
    content = { /* host Image/Icon */ },
    accessibility = SKAccessibilityConfig(contentDescription = "Photo"),
)
```

Uses `SKAppearanceConfig.Avatar` (size via appearance `SKSize`).

## Accessibility

- Announces `contentDescription`, else initials
- Without either, treated as decorative (`clearAndSetSemantics` / `IMPORTANT_FOR_ACCESSIBILITY_NO`)
- Nested image/initials views are not announced separately (XML)

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Image | optional `@Composable content` | `setImage(Drawable?)` |
| Fallback | up to 2 uppercase initials | same |

**Deferred:** remote loading, avatar groups, status-ring overlays, profile framework.
