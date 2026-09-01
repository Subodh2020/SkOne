# SKBottomAppBar

Bottom application chrome — Compose `SKBottomAppBar` / XML `SKBottomAppBarView`.

```kotlin
SKBottomAppBar(
    leading = { SKIconButton(...) },
    content = { SKText("Ready") },
    trailing = { SKButton(text = "Save", onClick = { }) },
)
```

Uses `SKAppearanceConfig.BottomAppBar`.

## FAB note

Optional `floatingActionButton` / `setFloatingActionButton` is a **simple layout slot** inside the bar.
For a FAB floating over content, prefer `SKScaffold(floatingActionButton = …)`.
No scroll-hide, docking animation, or auto FAB coordination.

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Slots | composable lambdas | `leadingContainer` / `contentContainer` / `trailingContainer` / `fabContainer` |

**Deferred:** navigation graph, hide-on-scroll, FAB animation, state restoration.
