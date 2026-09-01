# SKBottomSheet

Lean host-controlled bottom sheet — Compose `SKBottomSheet` / XML `SKBottomSheetView`.

```kotlin
SKBottomSheet(
    visible = open,
    onDismissRequest = { open = false },
    title = "Filters",
    primaryActionLabel = "Apply",
    onPrimaryAction = { /* … */ },
    secondaryActionLabel = "Reset",
    onSecondaryAction = { /* … */ },
    secondaryEnabled = true,
) {
    SKCheckbox(checked = unreadOnly, onCheckedChange = { unreadOnly = it }, label = "Unread only")
}
```

Uses `SKAppearanceConfig.BottomSheet`.

## Behavior

- Host owns `visible`
- Back / outside dismiss where the platform dialog supports it
- Optional primary / secondary actions with enabled flags
- No sheet manager, gesture framework, or navigation integration

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Surface | Bottom-aligned `Dialog` + scrim | Bottom-gravity `Dialog` |
| Content | Composable lambda | `setSheetContent(View)` + `show()` / `dismiss()` |
| Drag-to-dismiss | Not implemented | Not implemented |

**Deferred:** Material ModalBottomSheet gestures, nested scrolling, sheet stack manager, predictive back animations.
