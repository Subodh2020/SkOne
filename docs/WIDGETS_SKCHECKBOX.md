# SKCheckbox

Binary checkbox — Compose `SKCheckbox` / XML `SKCheckboxView`.

**Deferred:** indeterminate state.

```kotlin
SKCheckbox(
    checked = accepted,
    onCheckedChange = { accepted = it },
    label = "Accept terms",
    enabled = true,
    accessibility = SKAccessibilityConfig(testTag = "accept"),
)
```

Uses `SKAppearanceConfig.Toggle`. Role defaults to `checkbox`. State description includes Checked/Unchecked.
