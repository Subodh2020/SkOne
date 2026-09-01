# XML application recipes

Practical XML / View composition for the same three Application Example flows as Compose.

**Scope:** in-tree public `skone-xml` APIs (`SK*View`).  
**Not a Maven Central `alpha01` claim** unless those views are listed as published in [CONSUMER_GUIDE.md](../CONSUMER_GUIDE.md).

Companion Compose proof: Playground Application Examples + [SCREEN_COMPOSITION.md](SCREEN_COMPOSITION.md).

Setter names below match the **current public XML APIs** (verified against `skone-xml` sources).

## Shared rules

1. Host Activity/Fragment owns state (query, selection, sheet visibility, form values, destination).
2. Call public setters / listeners only — no `implementation` packages, no playground helpers.
3. Theme: `SKThemeHelper.install(SKThemes.Light)` (or Dark) before inflating SK views.
4. Activity theme: `AppCompatActivity` hosts need `Theme.AppCompat` (or descendant). Platform `android:Theme.Material.*` crashes at `setContentView`.
5. Optional: `view.bind(runtime)` when using `SKComponentRuntime`.
6. Accessibility: `setAccessibility(SKAccessibilityConfig(testTag = …, contentDescription = …))`.

**Availability:** recipes below use the **in-tree / intended alpha02** XML surface (`SKScaffoldView`, sheets, nav, …). Flagship-only published `alpha01` is `SKTextView` / `SKTextFieldView` / `SKThemeHelper` — see [CONSUMER_GUIDE.md](../CONSUMER_GUIDE.md).

Intentional Compose/XML differences (not bugs):

| Topic | Compose | XML |
|-------|---------|-----|
| Screen shell | `SKScaffold { }` slots | `SKScaffoldView` containers (`topBarContainer`, `contentContainer`, …) |
| Sheet | `visible` + content lambda | `SKBottomSheetView.show()` / `dismiss()` + `setSheetContent` |
| Overlays | Compose `Dialog` / `Popup` | Platform `Dialog` / `PopupWindow` |
| Naming | `title` / `selectedId` params | `setBarTitle` / `setSelectedItemId` / `setSelectedTabId` |
| Dropdown placement | Host places `SKDropdownMenu` near trigger (no anchor API) | `showAsDropDown(anchor)` |
| Icon-only a11y | Compose `require`s contentDescription | XML applies CD when present (document both) |

---

## 1. List + Search + Filter

**Product flow:** Top bar → tabs → search → list / empty / loading → FAB → filter sheet → snackbar.

```kotlin
SKThemeHelper.install(SKThemes.Light)

val scaffold = SKScaffoldView(context)
val topBar = SKTopAppBarView(context).apply {
    setBarTitle("Directory")
    setAccessibility(SKAccessibilityConfig(testTag = "xml_list_topbar"))
}
val tabs = SKTabRowView(context).apply {
    setTabItems(listOf(SKTabItem("all", "All"), SKTabItem("team", "Team")))
    setSelectedTabId("all")
    setOnSelectListener { selectedTab = it }
}
val search = SKSearchBarView(context).apply {
    setPlaceholder("Search people")
    setOnQueryChangeListener { query = it; refreshList() }
    setOnClearListener { query = ""; refreshList() }
    setAccessibility(SKAccessibilityConfig(testTag = "xml_list_search"))
}
val empty = SKEmptyStateView(context).apply {
    setTitle("No people match")
    setPrimaryAction("Clear search") { query = ""; refreshList() }
}
val loading = SKProgressIndicatorView(context).apply {
    setIndeterminateMode(true)
}
val fab = SKFabView(context).apply {
    setIcon(SKIconKey("skone.icon.add", contentDescription = "Open filters"))
    setOnFabClickListener { sheet.show() }
}
val snack = SKSnackbarView(context).apply {
    setMessage("Filters updated")
    setSnackbarVisible(false)
}

val sheet = SKBottomSheetView(context).apply {
    setSheetTitle("Filters")
    setPrimaryAction("Apply", enabled = true) {
        appliedUnreadOnly = draftUnreadOnly
        dismiss()
        snack.setMessage("Filters updated")
        snack.setSnackbarVisible(true)
    }
    setSecondaryAction("Reset") {
        draftUnreadOnly = false
        appliedUnreadOnly = false
        dismiss()
    }
    setSheetContent(
        SKCheckboxView(context).apply {
            setLabel("Unread only")
            setChecked(draftUnreadOnly)
            setOnCheckedChangeListener { draftUnreadOnly = it }
        },
    )
    setOnDismissListener { /* sync draft from applied */ }
}

// topBarContainer / bottomBarContainer are FrameLayouts — wrap multi-child chrome
// in a vertical LinearLayout (or use setTopBar once with a composed root).
val topChrome = LinearLayout(context).apply {
    orientation = LinearLayout.VERTICAL
    addView(topBar)
    addView(tabs)
}
scaffold.topBarContainer.addView(topChrome)
val content = LinearLayout(context).apply {
    orientation = LinearLayout.VERTICAL
    addView(search)
    // add SKListItemView rows, or empty/loading
}
scaffold.contentContainer.addView(ScrollView(context).apply { addView(content) })
scaffold.fabContainer.addView(fab)
scaffold.snackbarContainer.addView(snack)
```

**Disabled filter option:** `SKCheckboxView.setControlEnabled(false)` or a disabled segment on `SKSegmentedButtonView`.

---

## 2. Form + Validation

**Product flow:** Top bar → section header → fields → toggles → submit (loading) → snackbar / discard dialog.

```kotlin
val form = SKFormController.create()
val runtime = SKComponentRuntime.create()

val name = SKTextFieldView(context).apply {
    setFieldId("displayName")
    setLabel("Display name")
    setRequired(true)
    bind(runtime = runtime, form = form)
}
val email = SKTextFieldView(context).apply {
    setFieldId("email")
    setLabel("Email")
    setRequired(true)
    bind(runtime = runtime, form = form)
}
val title = SKTextFieldView(context).apply {
    setLabel("Job title")
    setFieldEnabled(false) // admin-managed
}
val notifications = SKSwitchView(context).apply {
    setLabel("Email notifications")
    setChecked(true)
    setOnCheckedChangeListener { /* host state */ }
}
val submit = SKButtonView(context).apply {
    setSkText("Save profile")
    setOnSkClickListener {
        if (!form.validate().isValid) {
            snack.setMessage("Fix validation errors")
            snack.setSnackbarVisible(true)
            return@setOnSkClickListener
        }
        setButtonEnabled(false)
        setLoading(true)
        // host async save → success/failure snackbar → re-enable
    }
}

val discard = SKAlertDialogHost(context) // show() when discard requested

val scaffold = SKScaffoldView(context)
scaffold.topBarContainer.addView(
    SKTopAppBarView(context).apply { setBarTitle("Profile settings") },
)
scaffold.contentContainer.addView(/* vertical LinearLayout of fields */)
scaffold.snackbarContainer.addView(SKSnackbarView(context))
```

Host owns submitting flag so duplicate taps cannot double-submit.

---

## 3. App Shell + Navigation

**Product flow:** Top bar + overflow menu → destination content → optional bottom app bar → navigation bar → FAB.

```kotlin
var destination = "home"

val scaffold = SKScaffoldView(context)
val menu = SKDropdownMenuView(context).apply {
    setMenuItems(
        listOf(
            SKMenuItem("refresh", "Refresh"),
            SKMenuItem("help", "Help"),
            SKMenuItem("signout", "Sign out", enabled = false),
        ),
    )
    setOnSelectListener { /* handle */ }
    setOnDismissListener { /* host clears expanded */ }
}
val topBar = SKTopAppBarView(context).apply {
    setBarTitle("Home")
    setActionIcon(SKIconKey("skone.icon.more", contentDescription = "More actions")) {
        menu.showAsDropDown(this)
    }
}
val nav = SKNavigationBarView(context).apply {
    setNavigationItems(
        listOf(
            SKNavigationItem("home", "Home"),
            SKNavigationItem("activity", "Activity"),
            SKNavigationItem("settings", "Settings"),
        ),
    )
    setSelectedItemId(destination)
    setOnSelectListener { id ->
        destination = id
        topBar.setBarTitle(id.replaceFirstChar { it.uppercase() })
        renderDestination()
    }
    setAccessibility(SKAccessibilityConfig(testTag = "xml_shell_nav"))
}
val bottomBar = SKBottomAppBarView(context) // Home only
val fab = SKFabView(context).apply {
    setIcon(SKIconKey("skone.icon.add", contentDescription = "Compose"))
    setOnFabClickListener { /* snackbar */ }
}

scaffold.topBarContainer.addView(topBar)
val bottomChrome = LinearLayout(context).apply {
    orientation = LinearLayout.VERTICAL
    addView(bottomBar) // conditional by destination
    addView(nav)
}
scaffold.bottomBarContainer.addView(bottomChrome)
scaffold.fabContainer.addView(fab)
// contentContainer: ScrollView + column of SKCardView / SKListItemView / SKTabRowView
```

Navigation stays local (host `destination` string). No SKOne navigation framework.

**Section headers:** XML uses `setHeaderTitle(...)` (not `setHeadline` — that API is on `SKListItemView`).

---

## Parity checklist

| Behavior | Compose example | XML recipe |
|----------|-----------------|------------|
| Host-owned sheet | `visible` / `onDismissRequest` | `show()` / `dismiss()` / dismiss listener |
| Host-owned nav | `selectedId` / `onSelect` | `setSelectedItemId` / `setOnSelectListener` |
| Empty / loading | `SKEmptyState` / `SKProgressIndicator` | `SKEmptyStateView` / `setIndeterminateMode` |
| Form validate | `ProvideSKFormController` + `validate()` | `SKTextFieldView.bind(runtime, form)` + `form.validate()` |
| Button loading | `loading = true` | `setLoading(true)` + `setButtonEnabled(false)` |
| testTag | `SKAccessibilityConfig` | same config |

## Naming consistency note (P2)

XML setters intentionally use prefixes (`setBarTitle`, `setSkText`, `setButtonEnabled`) to avoid clashing with Android `View` APIs. Product semantics match Compose; names differ by platform convention. Do **not** rename for aesthetics in an alpha without a migration plan.

## Deferred

- Full layout XML resource samples in a dedicated sample module
- Lazy list / RecyclerView SKOne wrapper
- Anchor tooltip as a wrapper around another control
