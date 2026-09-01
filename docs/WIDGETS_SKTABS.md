# SKTabs / SKTabRow

Exclusive tab selection — Compose `SKTabRow` / `SKTabs` / XML `SKTabRowView`.

Uses the same **items + selectedId + onSelect** pattern as `SKNavigationBar` — not a new selection framework.

```kotlin
SKTabs(
    items = listOf(
        SKTabItem("all", "All"),
        SKTabItem("unread", "Unread"),
        SKTabItem("archived", "Archived", enabled = false),
    ),
    selectedId = selected,
    onSelect = { selected = it },
    accessibility = SKAccessibilityConfig(testTag = "tabs"),
)
```

Uses `SKAppearanceConfig.TabRow`.

## Behavior

- Exclusive selection by tab `id`
- Per-tab `enabled` (in addition to row-level `enabled`)
- Optional decorative icon on `SKTabItem` (silent unless `contentDescription` set)
- Lightweight selected indicator (not an animation framework)

## Accessibility

- Each tab: `Role.Tab`, selected/unselected stateDescription, label as contentDescription
- Disabled tabs expose disabled semantics
- Container does not duplicate per-tab announcements (XML sets container `IMPORTANT_FOR_ACCESSIBILITY_NO`)
- testTag: `{parent}_{id}` or `tab_{id}`

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| API | `SKTabRow` + alias `SKTabs` | `SKTabRowView` + `setTabItems` |
| Indicator | Thin underline under selected tab | Bold selected label (no animated indicator) |

**Deferred:** pager/ViewPager, swipe, scrollable tabs, animated indicator framework, navigation-graph wiring.
