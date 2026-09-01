# SKScaffold

Lightweight screen shell — Compose `SKScaffold` / XML `SKScaffoldView`.

```kotlin
SKScaffold(
    topBar = { SKTopAppBar(title = "Inbox", ...) },
    bottomBar = { SKNavigationBar(...) },
    snackbar = { SKSnackbar(message = "...", visible = show) },
    floatingActionButton = { SKFab(icon = ..., onClick = { }) },
) {
    SKSectionHeader(title = "Today")
    SKListItem(headline = "…", onClick = { })
}
```

## Insets

- Compose: `skSafeDrawingPadding()` applied by default (`contentSafeDrawing = true`)
- XML: `skApplySystemBarPadding()` / `app:skSafeDrawing` (default true)

**Not included:** navigation framework, overlay manager, snackbar queue, scroll-aware FAB.

**Intentional surface difference:** Compose uses composable slots; XML uses container setters (`setTopBar` / `setContent` / `setBottomBar` / `setSnackbar` / `setFloatingActionButton`) or the public `*Container` FrameLayouts.

**XML multi-child chrome:** `topBarContainer`, `bottomBarContainer`, and `contentContainer` are `FrameLayout`s (overlay-friendly for snackbar/FAB siblings). If you need stacked chrome (for example top app bar + tab row, or bottom app bar + navigation bar), wrap children in a vertical `LinearLayout` before `addView`, or pass a single composed root through `setTopBar` / `setBottomBar` / `setContent`.
