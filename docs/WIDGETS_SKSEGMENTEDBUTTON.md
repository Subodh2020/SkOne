# SKSegmentedButton

Exclusive segmented selection — Compose `SKSegmentedButton` / XML `SKSegmentedButtonView`.

```kotlin
SKSegmentedButton(
    items = listOf(
        SKSegmentItem("all", "All"),
        SKSegmentItem("team", "Team"),
        SKSegmentItem("starred", "Starred", enabled = false),
    ),
    selectedId = selected,
    onSelect = { selected = it },
)
```

Uses `SKAppearanceConfig.SegmentedButton`.

Requires **at least two** segments (Compose/XML enforce).

## Accessibility

- Segments use radio semantics + selected/unselected stateDescription
- Disabled segments exposed as disabled
- Decorative leading icons silent unless explicit CD
- testTag: `{parent}_{id}` or `segment_{id}`

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| API | `items` + `onSelect` | `setSegmentItems` + `setOnSelectListener` |
| Selected chrome | Primary fill | Primary background color |

**Deferred:** multi-select, animated indicator framework, pager/swipe integration.
