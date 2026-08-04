package io.skone.playground.codegen

import io.skone.playground.editor.SkTextEditorState
import io.skone.playground.editor.SkTextFieldEditorState

/**
 * Generates copy-paste Compose snippets from live editor state.
 */
object ComposeCodeGenerator {
    fun skText(state: SkTextEditorState): String = buildString {
        appendLine("SKText(")
        appendLine("    text = \"${escape(state.text)}\",")
        appendLine("    appearance = SKAppearanceConfig.Text.copy(")
        appendLine("        typographyRole = SKTypographyRole.${state.typographyRole.name},")
        appendLine("        contentColorRole = SKColorRole.${state.contentColorRole.name},")
        appendLine("    ),")
        appendLine("    maxLines = ${state.maxLines},")
        appendLine("    softWrap = ${state.softWrap},")
        appendLine("    overflow = SKTextOverflow.${state.overflow.name},")
        appendLine("    textAlign = SKTextAlign.${state.textAlign.name},")
        append(")")
    }

    fun skTextField(state: SkTextFieldEditorState): String = buildString {
        appendLine("var value by remember { mutableStateOf(\"${escape(state.value)}\") }")
        appendLine("SKTextField(")
        appendLine("    value = value,")
        appendLine("    onValueChange = { value = it },")
        appendLine("    fieldId = \"email\",")
        appendLine("    label = \"${escape(state.label)}\",")
        appendLine("    hint = \"${escape(state.hint)}\",")
        appendLine("    supportingText = \"${escape(state.supportingText)}\",")
        appendLine("    required = ${state.required},")
        appendLine("    enabled = ${state.enabled},")
        appendLine("    readOnly = ${state.readOnly},")
        appendLine("    singleLine = ${state.singleLine},")
        appendLine("    imeAction = SKImeAction.${state.imeAction.name},")
        appendLine("    keyboardType = SKKeyboardType.${state.keyboardType.name},")
        append(")")
    }

    private fun escape(value: String): String =
        value.replace("\\", "\\\\").replace("\"", "\\\"")
}
