package io.skone.playground.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKAlertDialog
import io.skone.compose.widget.SKAvatar
import io.skone.compose.widget.SKBadge
import io.skone.compose.widget.SKBottomAppBar
import io.skone.compose.widget.SKBottomSheet
import io.skone.compose.widget.SKButton
import io.skone.compose.widget.SKCard
import io.skone.compose.widget.SKCheckbox
import io.skone.compose.widget.SKChip
import io.skone.compose.widget.SKDialog
import io.skone.compose.widget.SKDivider
import io.skone.compose.widget.SKDropdownMenu
import io.skone.compose.widget.SKEmptyState
import io.skone.compose.widget.SKFab
import io.skone.compose.widget.SKIconButton
import io.skone.compose.widget.SKListItem
import io.skone.compose.widget.SKMenu
import io.skone.compose.widget.SKNavigationBar
import io.skone.compose.widget.SKProgressIndicator
import io.skone.compose.widget.SKRadioButton
import io.skone.compose.widget.SKRadioGroup
import io.skone.compose.widget.SKScaffold
import io.skone.compose.widget.SKSearchBar
import io.skone.compose.widget.SKSectionHeader
import io.skone.compose.widget.SKSegmentedButton
import io.skone.compose.widget.SKSlider
import io.skone.compose.widget.SKSnackbar
import io.skone.compose.widget.SKSwitch
import io.skone.compose.widget.SKTabs
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.compose.widget.SKTooltip
import io.skone.compose.widget.SKTopAppBar
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.playground.catalog.CatalogId
import io.skone.playground.catalog.PlaygroundCatalog
import io.skone.playground.codegen.ComposeCodeGenerator
import io.skone.playground.codegen.XmlCodeGenerator
import io.skone.playground.editor.SkTextEditorState
import io.skone.playground.editor.SkTextFieldEditorState
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.feedback.SKProgressStyle
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import io.skone.ui.layout.SKDividerOrientation
import io.skone.ui.navigation.SKNavigationItem
import io.skone.ui.overlay.SKMenuItem
import io.skone.ui.overlay.SKSegmentItem
import io.skone.ui.text.SKTextAlign
import io.skone.ui.text.SKTextOverflow

@Composable
fun WidgetDetailScreen(widgetId: String) {
    when (widgetId) {
        CatalogId.SkText.route -> SkTextEditorScreen()
        CatalogId.SkTextField.route -> SkTextFieldEditorScreen()
        CatalogId.SkButton.route -> SkButtonEditorScreen()
        CatalogId.SkCheckbox.route -> SkCheckboxEditorScreen()
        CatalogId.SkSwitch.route -> SkSwitchEditorScreen()
        CatalogId.SkIconButton.route -> SkIconButtonEditorScreen()
        CatalogId.SkRadioButton.route, CatalogId.SkRadioGroup.route -> SkRadioEditorScreen()
        CatalogId.SkChip.route -> SkChipEditorScreen()
        CatalogId.SkSlider.route -> SkSliderEditorScreen()
        CatalogId.SkProgressIndicator.route -> SkProgressEditorScreen()
        CatalogId.SkDivider.route -> SkDividerEditorScreen()
        CatalogId.SkCard.route -> SkCardEditorScreen()
        CatalogId.SkSnackbar.route -> SkSnackbarEditorScreen()
        CatalogId.SkDialog.route -> SkDialogEditorScreen()
        CatalogId.SkAlertDialog.route -> SkAlertDialogEditorScreen()
        CatalogId.SkTopAppBar.route -> SkTopAppBarEditorScreen()
        CatalogId.SkNavigationBar.route -> SkNavigationBarEditorScreen()
        CatalogId.SkListItem.route,
        CatalogId.SkSectionHeader.route,
        CatalogId.SkScaffold.route,
        CatalogId.SkSearchBar.route,
        CatalogId.SkEmptyState.route,
        CatalogId.SkFab.route,
        -> SkScaffoldScreenEditor()
        CatalogId.SkTabRow.route,
        CatalogId.SkBadge.route,
        CatalogId.SkAvatar.route,
        -> SkChromeScreenEditor()
        CatalogId.SkMenu.route,
        CatalogId.SkDropdownMenu.route,
        CatalogId.SkTooltip.route,
        CatalogId.SkBottomAppBar.route,
        -> SkOverlayChromeScreenEditor()
        CatalogId.SkBottomSheet.route,
        CatalogId.SkSegmentedButton.route,
        -> SkSettingsFilterScreenEditor()
        else -> {
            val entry = PlaygroundCatalog.entries.firstOrNull { it.id.route == widgetId }
            PlaygroundBody(entry?.description ?: "Unknown widget: $widgetId")
        }
    }
}

@Composable
private fun SkTextEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var state by remember { mutableStateOf(SkTextEditorState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKText — live editor")
        PlaygroundSectionTitle("Preview")
        SKText(
            text = state.text,
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = state.typographyRole,
                contentColorRole = state.contentColorRole,
            ),
            maxLines = state.maxLines,
            softWrap = state.softWrap,
            overflow = state.overflow,
            textAlign = state.textAlign,
        )

        PlaygroundSectionTitle("Properties")
        EditorTextField("text", state.text) { state = state.copy(text = it) }
        EnumChipRow(
            label = "typography",
            options = listOf(
                SKTypographyRole.BodyLarge,
                SKTypographyRole.TitleMedium,
                SKTypographyRole.HeadlineSmall,
                SKTypographyRole.LabelLarge,
            ),
            selected = state.typographyRole,
            onSelected = { state = state.copy(typographyRole = it) },
        )
        EnumChipRow(
            label = "color",
            options = listOf(
                SKColorRole.OnSurface,
                SKColorRole.Primary,
                SKColorRole.Secondary,
                SKColorRole.Error,
            ),
            selected = state.contentColorRole,
            onSelected = { state = state.copy(contentColorRole = it) },
        )
        EnumChipRow(
            label = "overflow",
            options = SKTextOverflow.entries,
            selected = state.overflow,
            onSelected = { state = state.copy(overflow = it) },
        )
        EnumChipRow(
            label = "align",
            options = SKTextAlign.entries,
            selected = state.textAlign,
            onSelected = { state = state.copy(textAlign = it) },
        )
        EditorSwitch("softWrap", state.softWrap) { state = state.copy(softWrap = it) }

        PlaygroundSectionTitle("Generated code")
        CodeBlock("Compose", ComposeCodeGenerator.skText(state))
        CodeBlock("XML", XmlCodeGenerator.skText(state))
    }
}

@Composable
private fun SkTextFieldEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var state by remember { mutableStateOf(SkTextFieldEditorState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKTextField — live editor")
        PlaygroundSectionTitle("Preview")
        SKTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.value,
            onValueChange = { state = state.copy(value = it) },
            fieldId = "editor_email",
            label = state.label,
            hint = state.hint,
            supportingText = state.supportingText,
            required = state.required,
            enabled = state.enabled,
            readOnly = state.readOnly,
            singleLine = state.singleLine,
            imeAction = state.imeAction,
            keyboardType = state.keyboardType,
        )

        PlaygroundSectionTitle("Properties")
        EditorTextField("label", state.label) { state = state.copy(label = it) }
        EditorTextField("hint", state.hint) { state = state.copy(hint = it) }
        EditorTextField("supportingText", state.supportingText) {
            state = state.copy(supportingText = it)
        }
        EditorSwitch("required", state.required) { state = state.copy(required = it) }
        EditorSwitch("enabled", state.enabled) { state = state.copy(enabled = it) }
        EditorSwitch("readOnly", state.readOnly) { state = state.copy(readOnly = it) }
        EditorSwitch("singleLine", state.singleLine) { state = state.copy(singleLine = it) }
        EnumChipRow(
            label = "ime",
            options = listOf(SKImeAction.Next, SKImeAction.Done, SKImeAction.Search, SKImeAction.Go),
            selected = state.imeAction,
            onSelected = { state = state.copy(imeAction = it) },
        )
        EnumChipRow(
            label = "keyboard",
            options = listOf(
                SKKeyboardType.Text,
                SKKeyboardType.Email,
                SKKeyboardType.Phone,
                SKKeyboardType.Number,
                SKKeyboardType.Password,
            ),
            selected = state.keyboardType,
            onSelected = { state = state.copy(keyboardType = it) },
        )

        PlaygroundSectionTitle("Generated code")
        CodeBlock("Compose", ComposeCodeGenerator.skTextField(state))
        CodeBlock("XML", XmlCodeGenerator.skTextField(state))
    }
}

@Composable
private fun SkButtonEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var label by remember { mutableStateOf("Continue") }
    var enabled by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }
    var clicks by remember { mutableStateOf(0) }
    var variant by remember { mutableStateOf(SKAppearanceConfig.Button) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKButton — live editor")
        PlaygroundSectionTitle("Preview")
        SKButton(
            text = label,
            enabled = enabled,
            loading = loading,
            appearance = variant,
            onClick = { clicks++ },
        )
        SKText(text = "Clicks: $clicks")

        PlaygroundSectionTitle("Properties")
        EditorTextField("text", label) { label = it }
        EditorSwitch("enabled", enabled) { enabled = it }
        EditorSwitch("loading", loading) { loading = it }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "Filled" to SKAppearanceConfig.Button,
                "Tonal" to SKAppearanceConfig.ButtonTonal,
                "Outlined" to SKAppearanceConfig.ButtonOutlined,
                "Text" to SKAppearanceConfig.ButtonText,
            ).forEach { (name, appearance) ->
                FilterChip(
                    selected = variant == appearance,
                    onClick = { variant = appearance },
                    label = { Text(name) },
                )
            }
        }

        PlaygroundSectionTitle("Generated code")
        CodeBlock(
            "Compose",
            """
            SKButton(
                text = "$label",
                enabled = $enabled,
                loading = $loading,
                appearance = SKAppearanceConfig.${when (variant) {
                SKAppearanceConfig.ButtonTonal -> "ButtonTonal"
                SKAppearanceConfig.ButtonOutlined -> "ButtonOutlined"
                SKAppearanceConfig.ButtonText -> "ButtonText"
                else -> "Button"
            }},
                onClick = { /* … */ },
            )
            """.trimIndent(),
        )
    }
}

@Composable
private fun SkCheckboxEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var checked by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var label by remember { mutableStateOf("Accept terms") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKCheckbox")
        SKCheckbox(
            checked = checked,
            onCheckedChange = { checked = it },
            enabled = enabled,
            label = label,
        )
        EditorTextField("label", label) { label = it }
        EditorSwitch("enabled", enabled) { enabled = it }
        EditorSwitch("checked", checked) { checked = it }
    }
}

@Composable
private fun SkSwitchEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var checked by remember { mutableStateOf(true) }
    var enabled by remember { mutableStateOf(true) }
    var label by remember { mutableStateOf("Notifications") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKSwitch")
        SKSwitch(
            checked = checked,
            onCheckedChange = { checked = it },
            enabled = enabled,
            label = label,
        )
        EditorTextField("label", label) { label = it }
        EditorSwitch("enabled", enabled) { enabled = it }
        EditorSwitch("checked", checked) { checked = it }
    }
}

@Composable
private fun SkIconButtonEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var enabled by remember { mutableStateOf(true) }
    var clicks by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKIconButton")
        SKIconButton(
            icon = SKIconKey("skone.icon.close", contentDescription = "Close"),
            enabled = enabled,
            onClick = { clicks++ },
        )
        SKText(text = "Clicks: $clicks")
        EditorSwitch("enabled", enabled) { enabled = it }
    }
}

@Composable
private fun SkRadioEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var selected by remember { mutableStateOf<String?>("light") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKRadioGroup + SKRadioButton")
        SKRadioGroup(
            selectedValue = selected,
            onSelectedChange = { selected = it },
        ) {
            SKRadioButton(value = "light", label = "Light")
            SKRadioButton(value = "dark", label = "Dark")
            SKRadioButton(value = "system", label = "System")
        }
        SKText(text = "Selected: ${selected ?: "none"}")
    }
}

@Composable
private fun SkChipEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var selected by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKChip")
        SKChip(
            label = "Compose",
            selected = selected,
            enabled = enabled,
            leadingIcon = SKIconKey("skone.icon.tag"),
            onClick = { selected = !selected },
        )
        EditorSwitch("selected", selected) { selected = it }
        EditorSwitch("enabled", enabled) { enabled = it }
    }
}

@Composable
private fun SkSliderEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var value by remember { mutableFloatStateOf(0.4f) }
    var enabled by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKSlider")
        SKSlider(
            value = value,
            onValueChange = { value = it },
            valueRange = 0f..1f,
            steps = 4,
            enabled = enabled,
        )
        SKText(text = "Value: ${"%.2f".format(value)}")
        EditorSwitch("enabled", enabled) { enabled = it }
    }
}

@Composable
private fun SkProgressEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var progress by remember { mutableFloatStateOf(0.35f) }
    var indeterminate by remember { mutableStateOf(false) }
    var style by remember { mutableStateOf(SKProgressStyle.Linear) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKProgressIndicator")
        SKProgressIndicator(
            progress = progress,
            indeterminate = indeterminate,
            style = style,
        )
        EnumChipRow(
            label = "style",
            options = SKProgressStyle.entries,
            selected = style,
            onSelected = { style = it },
        )
        EditorSwitch("indeterminate", indeterminate) { indeterminate = it }
        SKSlider(
            value = progress,
            onValueChange = { progress = it },
            enabled = !indeterminate,
        )
    }
}

@Composable
private fun SkDividerEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var orientation by remember { mutableStateOf(SKDividerOrientation.Horizontal) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKDivider")
        if (orientation == SKDividerOrientation.Horizontal) {
            SKDivider(orientation = orientation)
        } else {
            Row(modifier = Modifier.height(48.dp)) {
                SKText(text = "A")
                SKDivider(orientation = SKDividerOrientation.Vertical)
                SKText(text = "B")
            }
        }
        EnumChipRow(
            label = "orientation",
            options = SKDividerOrientation.entries,
            selected = orientation,
            onSelected = { orientation = it },
        )
    }
}

@Composable
private fun SkCardEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var clicks by remember { mutableStateOf(0) }
    var clickable by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKCard")
        SKCard(
            onClick = if (clickable) {{ clicks++ }} else null,
            appearance = SKAppearanceConfig.Card,
        ) {
            SKText(text = "Card content")
            SKText(text = "Clicks: $clicks")
        }
        EditorSwitch("clickable", clickable) { clickable = it }
    }
}

@Composable
private fun SkSnackbarEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var visible by remember { mutableStateOf(true) }
    var actions by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKSnackbar")
        if (visible) {
            SKSnackbar(
                message = "Changes saved",
                actionLabel = "Undo",
                onAction = { actions++ },
            )
        }
        SKText(text = "Actions: $actions")
        EditorSwitch("visible", visible) { visible = it }
    }
}

@Composable
private fun SkDialogEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var open by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKDialog")
        SKButton(text = "Open dialog", onClick = { open = true })
        SKDialog(visible = open, onDismissRequest = { open = false }, title = "Details") {
            SKText(text = "Custom dialog content.")
        }
    }
}

@Composable
private fun SkAlertDialogEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var open by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("—") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKAlertDialog")
        SKButton(text = "Open alert", onClick = { open = true })
        SKText(text = "Result: $result")
        SKAlertDialog(
            visible = open,
            onConfirm = {
                result = "confirmed"
                open = false
            },
            onDismissRequest = {
                result = "dismissed"
                open = false
            },
            title = "Delete item?",
            message = "This action cannot be undone.",
        )
    }
}

@Composable
private fun SkTopAppBarEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var nav by remember { mutableStateOf(0) }
    var action by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKTopAppBar")
        SKTopAppBar(
            title = "Library",
            navigationIcon = SKIconKey("skone.icon.back", contentDescription = "Back"),
            onNavigationClick = { nav++ },
            actionIcon = SKIconKey("skone.icon.more", contentDescription = "More"),
            onActionClick = { action++ },
        )
        SKText(text = "Nav: $nav  Action: $action")
    }
}

@Composable
private fun SkNavigationBarEditorScreen() {
    val spacing = skTheme.tokens.spacing
    var selected by remember { mutableStateOf("home") }
    var enabled by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKNavigationBar")
        SKNavigationBar(
            items = listOf(
                SKNavigationItem("home", "Home"),
                SKNavigationItem("search", "Search"),
                SKNavigationItem("profile", "Profile"),
            ),
            selectedId = selected,
            onSelect = { selected = it },
            enabled = enabled,
        )
        SKText(text = "Selected: $selected")
        EditorSwitch("enabled", enabled) { enabled = it }
    }
}

@Composable
private fun SkScaffoldScreenEditor() {
    var selected by remember { mutableStateOf("search") }
    var snackVisible by remember { mutableStateOf(false) }
    var selectedRow by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    val allItems = remember {
        listOf(
            "Design review" to "Please review the latest SKOne widgets.",
            "Accessibility pass" to "TalkBack sweep for search and empty states.",
            "Release checklist" to "VERSION_NAME stays 1.4.0-alpha01 until publish.",
        )
    }
    val filtered = remember(query) {
        val q = query.trim()
        if (q.isEmpty()) allItems else allItems.filter {
            it.first.contains(q, ignoreCase = true) || it.second.contains(q, ignoreCase = true)
        }
    }
    SKScaffold(
        topBar = {
            SKTopAppBar(
                title = "Inbox",
                navigationIcon = SKIconKey("skone.icon.menu", contentDescription = "Menu"),
                onNavigationClick = {},
                actionIcon = SKIconKey("skone.icon.search", contentDescription = "Search"),
                onActionClick = {},
            )
        },
        bottomBar = {
            SKNavigationBar(
                items = listOf(
                    SKNavigationItem("home", "Home"),
                    SKNavigationItem("search", "Search"),
                    SKNavigationItem("profile", "Profile"),
                ),
                selectedId = selected,
                onSelect = { selected = it },
            )
        },
        snackbar = {
            SKSnackbar(
                message = "Draft saved",
                actionLabel = "Undo",
                onAction = { snackVisible = false },
                visible = snackVisible,
            )
        },
        floatingActionButton = {
            SKFab(
                icon = SKIconKey("skone.icon.add", contentDescription = "Compose"),
                onClick = { snackVisible = true },
                accessibility = SKAccessibilityConfig(testTag = "playground_fab"),
            )
        },
        contentSafeDrawing = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(skTheme.tokens.spacing.sm.toDp()),
            verticalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.xs.toDp()),
        ) {
            PlaygroundSectionTitle("Search → list → empty")
            SKSearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Search messages",
                onClear = { query = "" },
                accessibility = SKAccessibilityConfig(testTag = "playground_search"),
            )
            SKSectionHeader(
                title = if (query.isBlank()) "Today" else "Results",
                supportingText = if (query.isBlank()) "Priority messages" else "${filtered.size} match(es)",
                actionLabel = if (query.isNotBlank()) "Clear" else null,
                onAction = if (query.isNotBlank()) ({ query = "" }) else null,
            )
            if (filtered.isEmpty()) {
                SKEmptyState(
                    title = "No messages found",
                    description = "Try a different search or clear filters.",
                    icon = SKIconKey("skone.icon.empty"),
                    primaryActionLabel = "Clear search",
                    onPrimaryAction = { query = "" },
                    secondaryActionLabel = "Compose",
                    onSecondaryAction = { snackVisible = true },
                    accessibility = SKAccessibilityConfig(testTag = "playground_empty"),
                )
            } else {
                filtered.forEach { (headline, supporting) ->
                    SKListItem(
                        headline = headline,
                        supportingText = supporting,
                        trailingText = "9:41",
                        selected = selectedRow == headline,
                        onClick = {
                            selectedRow = headline
                            snackVisible = true
                        },
                    )
                }
            }
            SKListItem(
                headline = "Disabled row",
                supportingText = "Cannot open",
                enabled = false,
                onClick = {},
            )
        }
    }
}

@Composable
private fun SkChromeScreenEditor() {
    var nav by remember { mutableStateOf("home") }
    var tab by remember { mutableStateOf("all") }
    var selectedRow by remember { mutableStateOf<String?>(null) }
    val rows = remember(tab) {
        when (tab) {
            "unread" -> listOf(
                "Design review" to "3 unread comments",
                "Accessibility pass" to "TalkBack findings",
            )
            "archived" -> listOf(
                "Old release notes" to "Archived last week",
            )
            else -> listOf(
                "Design review" to "Please review the latest SKOne widgets.",
                "Accessibility pass" to "TalkBack sweep for tabs and badges.",
                "Release checklist" to "Keep VERSION_NAME at 1.4.0-alpha01.",
            )
        }
    }
    SKScaffold(
        topBar = {
            Column {
                SKTopAppBar(
                    title = "Team inbox",
                    navigationIcon = SKIconKey("skone.icon.menu", contentDescription = "Menu"),
                    onNavigationClick = {},
                    actionIcon = SKIconKey("skone.icon.more", contentDescription = "More"),
                    onActionClick = {},
                )
                SKTabs(
                    items = listOf(
                        SKTabItem("all", "All"),
                        SKTabItem("unread", "Unread"),
                        SKTabItem("archived", "Archived", enabled = false),
                    ),
                    selectedId = tab,
                    onSelect = { tab = it },
                    accessibility = SKAccessibilityConfig(testTag = "playground_tabs"),
                )
            }
        },
        bottomBar = {
            SKNavigationBar(
                items = listOf(
                    SKNavigationItem("home", "Home"),
                    SKNavigationItem("people", "People"),
                    SKNavigationItem("settings", "Settings"),
                ),
                selectedId = nav,
                onSelect = { nav = it },
            )
        },
        contentSafeDrawing = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(skTheme.tokens.spacing.sm.toDp()),
            verticalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.sm.toDp()),
        ) {
            PlaygroundSectionTitle("Tabs + Badge + Avatar")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.sm.toDp()),
            ) {
                SKAvatar(
                    initials = "SK",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Subodh Kumar",
                        testTag = "playground_avatar",
                    ),
                )
                Column(verticalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.xs.toDp())) {
                    Row(horizontalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.xs.toDp())) {
                        SKText(text = "Notifications")
                        SKBadge(
                            text = "3",
                            accessibility = SKAccessibilityConfig(testTag = "playground_badge"),
                        )
                        SKBadge(
                            dot = true,
                            accessibility = SKAccessibilityConfig(
                                contentDescription = "Live updates",
                                testTag = "playground_dot",
                            ),
                        )
                    }
                    SKText(text = "Selected tab: $tab")
                }
            }
            SKSectionHeader(title = "Messages", supportingText = "${rows.size} in view")
            rows.forEach { (headline, supporting) ->
                SKListItem(
                    headline = headline,
                    supportingText = supporting,
                    leadingIcon = SKIconKey("skone.icon.person"),
                    selected = selectedRow == headline,
                    onClick = { selectedRow = headline },
                )
            }
        }
    }
}

@Composable
private fun SkOverlayChromeScreenEditor() {
    var nav by remember { mutableStateOf("home") }
    var tab by remember { mutableStateOf("inbox") }
    var menuSelection by remember { mutableStateOf("none") }
    var dropdownOpen by remember { mutableStateOf(false) }
    var dropdownSelected by remember { mutableStateOf("filter_all") }
    var tipVisible by remember { mutableStateOf(false) }
    var selectedRow by remember { mutableStateOf<String?>(null) }
    val menuItems = remember {
        listOf(
            SKMenuItem("edit", "Edit"),
            SKMenuItem("share", "Share"),
            SKMenuItem("delete", "Delete", enabled = false),
        )
    }
    val filterItems = remember {
        listOf(
            SKMenuItem("filter_all", "All messages"),
            SKMenuItem("filter_unread", "Unread only"),
            SKMenuItem("filter_muted", "Muted", enabled = false),
        )
    }
    SKScaffold(
        topBar = {
            Column {
                SKTopAppBar(
                    title = "Workbench",
                    navigationIcon = SKIconKey("skone.icon.menu", contentDescription = "Menu"),
                    onNavigationClick = {},
                    actionIcon = SKIconKey("skone.icon.more", contentDescription = "More"),
                    onActionClick = { dropdownOpen = true },
                )
                SKTabs(
                    items = listOf(
                        SKTabItem("inbox", "Inbox"),
                        SKTabItem("sent", "Sent"),
                    ),
                    selectedId = tab,
                    onSelect = { tab = it },
                )
            }
        },
        bottomBar = {
            Column {
                SKBottomAppBar(
                    leading = {
                        SKIconButton(
                            icon = SKIconKey("skone.icon.info", contentDescription = "Help"),
                            onClick = { tipVisible = !tipVisible },
                            accessibility = SKAccessibilityConfig(testTag = "bab_help"),
                        )
                    },
                    content = {
                        SKText(text = if (tipVisible) "Tip visible" else "Ready")
                    },
                    trailing = {
                        SKButton(text = "Action", onClick = {})
                    },
                    accessibility = SKAccessibilityConfig(testTag = "playground_bab"),
                )
                SKNavigationBar(
                    items = listOf(
                        SKNavigationItem("home", "Home"),
                        SKNavigationItem("search", "Search"),
                        SKNavigationItem("profile", "Profile"),
                    ),
                    selectedId = nav,
                    onSelect = { nav = it },
                )
            }
        },
        floatingActionButton = {
            SKFab(
                icon = SKIconKey("skone.icon.add", contentDescription = "Compose"),
                onClick = {},
            )
        },
        contentSafeDrawing = false,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(skTheme.tokens.spacing.sm.toDp()),
                verticalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.sm.toDp()),
            ) {
                PlaygroundSectionTitle("Menu / Dropdown / Tooltip / BottomAppBar")
                SKTooltip(
                    message = "Long-press actions live in the overflow menu",
                    visible = tipVisible,
                    accessibility = SKAccessibilityConfig(testTag = "playground_tip"),
                )
                SKSectionHeader(title = "Inline menu", supportingText = "Last: $menuSelection")
                SKMenu(
                    items = menuItems,
                    onItemClick = { menuSelection = it },
                    accessibility = SKAccessibilityConfig(testTag = "playground_menu"),
                )
                SKButton(
                    text = "Open filter dropdown",
                    onClick = { dropdownOpen = true },
                )
                SKText(text = "Filter: $dropdownSelected")
                SKListItem(
                    headline = "Design review",
                    supportingText = "Opens with row selection",
                    selected = selectedRow == "design",
                    onClick = { selectedRow = "design" },
                )
                SKListItem(
                    headline = "Release checklist",
                    supportingText = "VERSION_NAME stays 1.4.0-alpha01",
                    selected = selectedRow == "release",
                    onClick = { selectedRow = "release" },
                )
            }
            SKDropdownMenu(
                expanded = dropdownOpen,
                onDismissRequest = { dropdownOpen = false },
                items = filterItems,
                selectedId = dropdownSelected,
                onItemClick = { dropdownSelected = it },
                accessibility = SKAccessibilityConfig(testTag = "playground_dd"),
            )
        }
    }
}

@Composable
private fun SkSettingsFilterScreenEditor() {
    var segment by remember { mutableStateOf("all") }
    var query by remember { mutableStateOf("") }
    var sheetOpen by remember { mutableStateOf(false) }
    var snackVisible by remember { mutableStateOf(false) }
    var unreadOnly by remember { mutableStateOf(false) }
    var selectedRow by remember { mutableStateOf<String?>(null) }
    val people = remember {
        listOf(
            "Alex Rivera" to "Product",
            "Sam Chen" to "Design",
            "Jordan Lee" to "Engineering",
        )
    }
    val filtered = remember(segment, query, unreadOnly) {
        people.filter { (name, role) ->
            val matchesQuery = query.isBlank() ||
                name.contains(query, ignoreCase = true) ||
                role.contains(query, ignoreCase = true)
            val matchesSegment = when (segment) {
                "team" -> role != "Product"
                "starred" -> name.startsWith("S")
                else -> true
            }
            matchesQuery && matchesSegment && (!unreadOnly || name.startsWith("A"))
        }
    }
    SKScaffold(
        topBar = {
            SKTopAppBar(
                title = "People",
                navigationIcon = SKIconKey("skone.icon.menu", contentDescription = "Menu"),
                onNavigationClick = {},
                actionIcon = SKIconKey("skone.icon.filter", contentDescription = "Filters"),
                onActionClick = { sheetOpen = true },
            )
        },
        snackbar = {
            SKSnackbar(
                message = "Filters applied",
                actionLabel = "Undo",
                onAction = {
                    unreadOnly = false
                    snackVisible = false
                },
                visible = snackVisible,
            )
        },
        floatingActionButton = {
            SKFab(
                icon = SKIconKey("skone.icon.add", contentDescription = "Invite"),
                onClick = { sheetOpen = true },
                accessibility = SKAccessibilityConfig(testTag = "settings_fab"),
            )
        },
        contentSafeDrawing = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(skTheme.tokens.spacing.sm.toDp()),
            verticalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.sm.toDp()),
        ) {
            PlaygroundSectionTitle("Settings / Filter integration")
            SKSegmentedButton(
                items = listOf(
                    SKSegmentItem("all", "All"),
                    SKSegmentItem("team", "Team"),
                    SKSegmentItem("starred", "Starred", enabled = false),
                ),
                selectedId = segment,
                onSelect = { segment = it },
                accessibility = SKAccessibilityConfig(testTag = "settings_seg"),
            )
            SKSearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Search people",
                onClear = { query = "" },
                accessibility = SKAccessibilityConfig(testTag = "settings_search"),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.sm.toDp()),
            ) {
                SKAvatar(
                    initials = "SK",
                    accessibility = SKAccessibilityConfig(contentDescription = "You", testTag = "settings_avatar"),
                )
                Column {
                    SKText(text = "Workspace members")
                    Row(horizontalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.xs.toDp())) {
                        SKText(text = "${filtered.size} visible")
                        if (unreadOnly) {
                            SKBadge(text = "1", accessibility = SKAccessibilityConfig(testTag = "settings_badge"))
                        }
                    }
                }
            }
            SKSectionHeader(
                title = "Directory",
                supportingText = "Segment: $segment",
                actionLabel = "Filters",
                onAction = { sheetOpen = true },
            )
            filtered.forEach { (name, role) ->
                SKListItem(
                    headline = name,
                    supportingText = role,
                    leadingIcon = SKIconKey("skone.icon.person"),
                    selected = selectedRow == name,
                    onClick = { selectedRow = name },
                )
            }
            if (filtered.isEmpty()) {
                SKEmptyState(
                    title = "No people match",
                    description = "Clear search or open filters.",
                    primaryActionLabel = "Clear search",
                    onPrimaryAction = { query = "" },
                )
            }
        }
    }
    SKBottomSheet(
        visible = sheetOpen,
        onDismissRequest = { sheetOpen = false },
        title = "Filters",
        primaryActionLabel = "Apply",
        onPrimaryAction = {
            snackVisible = true
        },
        secondaryActionLabel = "Reset",
        onSecondaryAction = {
            unreadOnly = false
            segment = "all"
            query = ""
        },
        accessibility = SKAccessibilityConfig(testTag = "settings_sheet"),
    ) {
        SKCheckbox(
            checked = unreadOnly,
            onCheckedChange = { unreadOnly = it },
            label = "Unread only",
            accessibility = SKAccessibilityConfig(testTag = "settings_unread"),
        )
        SKText(text = "Starred segment stays disabled to validate disabled selection.")
        SKButton(text = "Close without apply", onClick = { sheetOpen = false })
    }
}

@Composable
private fun <T : Enum<T>> EnumChipRow(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = skTheme.tokens.colors.onSurfaceVariant.toColor())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelected(option) },
                    label = { Text(option.name) },
                )
            }
        }
    }
}
