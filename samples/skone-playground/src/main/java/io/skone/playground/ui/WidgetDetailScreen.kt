package io.skone.playground.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.playground.catalog.CatalogId
import io.skone.playground.catalog.PlaygroundCatalog
import io.skone.playground.codegen.ComposeCodeGenerator
import io.skone.playground.codegen.XmlCodeGenerator
import io.skone.playground.editor.SkTextEditorState
import io.skone.playground.editor.SkTextFieldEditorState
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import io.skone.ui.text.SKTextAlign
import io.skone.ui.text.SKTextOverflow

@Composable
fun WidgetDetailScreen(widgetId: String) {
    when (widgetId) {
        CatalogId.SkText.route -> SkTextEditorScreen()
        CatalogId.SkTextField.route -> SkTextFieldEditorScreen()
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
