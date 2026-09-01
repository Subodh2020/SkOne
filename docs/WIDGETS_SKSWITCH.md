# SKSwitch

On/off switch — Compose `SKSwitch` / XML `SKSwitchView`.

```kotlin
SKSwitch(
    checked = enabled,
    onCheckedChange = { enabled = it },
    label = "Notifications",
    accessibility = SKAccessibilityConfig(testTag = "notif"),
)
```

Uses `SKAppearanceConfig.Toggle`. Role defaults to `switch`. State description includes On/Off.
