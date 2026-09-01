# SKFab

Minimal floating action button — Compose `SKFab` / XML `SKFabView`.

```kotlin
SKFab(
    icon = SKIconKey("skone.icon.add", contentDescription = "Compose"),
    onClick = { /* … */ },
    accessibility = SKAccessibilityConfig(testTag = "fab"),
)
```

Uses `SKAppearanceConfig.Fab`.

**Requires** a meaningful description via `accessibility.contentDescription` or `SKIconKey.contentDescription`.
Compose throws if both are missing. XML applies null description (incomplete configuration) — same contract as `SKIconButtonView`.

## Scaffold slot (optional)

`SKScaffold` / `SKScaffoldView` expose an optional FAB overlay (bottom-end over content):

- Compose: `floatingActionButton = { SKFab(...) }`
- XML: `setFloatingActionButton(view)`

Placement only — no scroll-aware hide, speed-dial, or extended FAB hierarchy.

**Not included:** positioning framework beyond the optional scaffold slot, animations, extended FAB variants.
