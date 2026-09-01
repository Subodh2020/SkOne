# SKDropdownMenu

Host-controlled dropdown — Compose `SKDropdownMenu` / XML `SKDropdownMenuView`.

```kotlin
SKDropdownMenu(
    expanded = open,
    onDismissRequest = { open = false },
    items = listOf(SKMenuItem("all", "All"), SKMenuItem("unread", "Unread")),
    selectedId = selected,
    onItemClick = { selected = it },
)
```

Uses `SKAppearanceConfig.DropdownMenu`.

## Behavior

- Outside click + back dismiss (Compose `Popup`; XML `PopupWindow`)
- Selecting an enabled item updates selection, closes, and fires callback
- Disabled items do not select
- Row-level `enabled = false` prevents expand

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Popup | `androidx.compose.ui.window.Popup` | `PopupWindow.showAsDropDown(anchor)` |
| Anchor | Parent composition | Explicit `showAsDropDown(anchor)` |

**Deferred:** cascading menus, searchable dropdown, animation framework, complex positioning engine.
