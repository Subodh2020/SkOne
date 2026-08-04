# SKTextField — Flagship Input Widget

`SKTextField` / `SKTextFieldView` is the reference input for all future SKOne fields.

## Capabilities

- Label, hint, supporting text
- Leading / trailing icons (`SKIconKey`)
- `enabled`, `readOnly`, `required`
- Input masks, formatters, validation rules
- IME actions + focus chain navigation
- Error and success visual states (token roles)
- Auto-register with `SKFormController`
- Accessibility, analytics, plugins, AI config
- Compose + XML

## Compose

```kotlin
val form = remember { SKFormController.create() }
ProvideSKFormController(form) {
    SKTextField(
        fieldId = "email",
        value = email,
        onValueChange = { email = it },
        label = "Email",
        hint = "name@company.com",
        required = true,
        rules = listOf(SKRequiredRule(), SKEmailRule()),
        formatter = SKTrimFormatter,
        imeAction = SKImeAction.Next,
    )
}
```

## XML

```xml
<io.skone.xml.widget.SKTextFieldView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:skFieldId="email"
    app:skLabel="Email"
    app:skHint="name@company.com"
    app:skRequired="true" />
```

```kotlin
fieldView.bind(runtime = componentRuntime, form = formController)
```

## Parameter order (Compose)

1. `modifier`
2. `value` / `fieldId`
3. `onValueChange`, `onImeAction`
4. label / hint / icons (content)
5. `appearance`
6. `enabled`, `readOnly`, `singleLine`, `imeAction`, mask/formatter
7. `rules`, `required`
8. `accessibility`
9. `analytics`
10. `ai`

## Related

- [ADR 0011](adr/0011-sktextfield-flagship-input.md)
- [Form Framework](FORM_FRAMEWORK.md)
- [Component Framework](COMPONENT_FRAMEWORK.md)

## Tests

| Layer | Location |
|-------|----------|
| Unit | `skone-ui/.../SKTextFieldComponentTest` |
| Compose UI + a11y | `skone-compose/androidTest/.../SKTextFieldComposeTest`, `SKTextFieldAccessibilityTest` |
| XML | `skone-xml/.../SKTextFieldViewTest` |
| Screenshots | `skone-compose/.../SKTextFieldScreenshotTest` (Paparazzi) |
