# SKMenu

Reusable menu surface — Compose `SKMenu` / XML `SKMenuView`.

Host owns visibility and placement. Items are actionable options (not a command/DSL framework).

```kotlin
SKMenu(
    items = listOf(
        SKMenuItem("edit", "Edit"),
        SKMenuItem("delete", "Delete", enabled = false),
    ),
    onItemClick = { id -> /* … */ },
    accessibility = SKAccessibilityConfig(testTag = "menu"),
)
```

Uses `SKAppearanceConfig.Menu`.

## Accessibility

- Container content description defaults to `"Menu"`
- Items: button role, label CD, disabled state
- Decorative leading icons silent unless `SKIconKey.contentDescription` set
- testTag: `{parent}_{id}` or `menu_{id}`

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Placement | Host wraps (e.g. in dropdown) | Inline view / host popup |
| Items | `List<SKMenuItem>` | `setMenuItems` |

**Deferred:** nested/cascading menus, keyboard shortcuts, command framework.
