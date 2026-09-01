# SKEmptyState

Empty / zero-results content block — Compose `SKEmptyState` / XML `SKEmptyStateView`.

Host supplies title, optional description, icon, and actions. **No** state-management or screen-state framework.

```kotlin
SKEmptyState(
    title = "No messages found",
    description = "Try a different search",
    icon = SKIconKey("skone.icon.empty"),
    primaryActionLabel = "Clear search",
    onPrimaryAction = { query = "" },
    secondaryActionLabel = "Compose",
    onSecondaryAction = { /* … */ },
)
```

Uses `SKAppearanceConfig.EmptyState`.

## Accessibility

- Title + description form the default content description when none is supplied
- Decorative illustration (icon without `contentDescription`) is not announced
- Primary / secondary actions expose button semantics and `{testTag}_primary` / `_secondary` tags

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Actions | Composable clickable labels | `setPrimaryAction` / `setSecondaryAction` |
| Layout | Centered `Column` | `LinearLayout` vertical |

**Not included:** loading / error state machine, illustration asset pipeline.
