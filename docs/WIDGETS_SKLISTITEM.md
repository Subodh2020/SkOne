# SKListItem

Reusable list row — Compose `SKListItem` / XML `SKListItemView`.

```kotlin
SKListItem(
    headline = "Inbox",
    supportingText = "3 unread",
    trailingText = "Now",
    leadingIcon = SKIconKey("skone.icon.mail"), // decorative unless CD set
    selected = false,
    onClick = { /* optional */ },
)
```

Long headline/supporting text ellipsize (1 / 2 lines). Leading icon decorative-by-default.
