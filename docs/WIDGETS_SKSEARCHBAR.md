# SKSearchBar

Reusable search input — Compose `SKSearchBar` / XML `SKSearchBarView`.

Host owns the query string. Reuses EditText / BasicTextField + IME Search patterns — **not** a second text-input framework and **not** auto-registered with `SKFormController`.

```kotlin
SKSearchBar(
    query = query,
    onQueryChange = { query = it },
    placeholder = "Search",
    onSearch = { /* IME search */ },
    onClear = { query = "" },
    accessibility = SKAccessibilityConfig(testTag = "search"),
)
```

Uses `SKAppearanceConfig.SearchBar`.

## Behavior

- Leading search glyph is decorative (not announced)
- Clear action appears when query is non-empty and enabled; accessible label `"Clear search"`
- Default IME action is Search
- Disabled state dims and blocks editing / clear

## Intentional Compose / XML differences

| Topic | Compose | XML |
|-------|---------|-----|
| Editable surface | `BasicTextField` | `AppCompatEditText` (`input`) |
| testTag | Semantics on editable node; clear uses `{tag}_clear` | Tag on `input`; clear uses `{tag}_clear` |
| Placeholder | Overlay text when empty | `EditText` hint |

**Not included:** search history, suggestions, scoped filters, form registration.
