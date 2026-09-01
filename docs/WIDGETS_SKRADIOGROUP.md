# SKRadioGroup

Exclusive selection container for radio options.

## Compose

```kotlin
var selected by remember { mutableStateOf<String?>("a") }
SKRadioGroup(selectedValue = selected, onSelectedChange = { selected = it }) {
    SKRadioButton(value = "a", label = "Alpha")
    SKRadioButton(value = "b", label = "Beta")
}
```

Uses a CompositionLocal scope — not a heavyweight selection framework.

## XML

```xml
<io.skone.xml.widget.SKRadioGroupView ...>
  <io.skone.xml.widget.SKRadioButtonView app:skValue="a" app:skLabel="Alpha" />
  <io.skone.xml.widget.SKRadioButtonView app:skValue="b" app:skLabel="Beta" />
</io.skone.xml.widget.SKRadioGroupView>
```

Shared logic: `SKRadioGroupController` in `skone-ui`.
