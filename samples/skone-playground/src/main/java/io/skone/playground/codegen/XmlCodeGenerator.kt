package io.skone.playground.codegen

import io.skone.playground.editor.SkTextEditorState
import io.skone.playground.editor.SkTextFieldEditorState
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import io.skone.ui.text.SKTextAlign
import io.skone.ui.text.SKTextOverflow

/**
 * Generates copy-paste XML snippets from live editor state.
 */
object XmlCodeGenerator {
    fun skText(state: SkTextEditorState): String = buildString {
        appendLine("<io.skone.xml.widget.SKTextView")
        appendLine("    android:layout_width=\"match_parent\"")
        appendLine("    android:layout_height=\"wrap_content\"")
        appendLine("    app:skText=\"${escapeXml(state.text)}\"")
        appendLine("    app:skMaxLines=\"${state.maxLines}\"")
        appendLine("    app:skSoftWrap=\"${state.softWrap}\"")
        appendLine("    app:skOverflow=\"${overflowAttr(state.overflow)}\"")
        appendLine("    app:skTextAlign=\"${alignAttr(state.textAlign)}\"")
        appendLine("    app:skTypographyRole=\"${camel(state.typographyRole.name)}\"")
        appendLine("    app:skContentColorRole=\"${camel(state.contentColorRole.name)}\" />")
    }

    fun skTextField(state: SkTextFieldEditorState): String = buildString {
        appendLine("<io.skone.xml.widget.SKTextFieldView")
        appendLine("    android:layout_width=\"match_parent\"")
        appendLine("    android:layout_height=\"wrap_content\"")
        appendLine("    app:skFieldId=\"email\"")
        appendLine("    app:skLabel=\"${escapeXml(state.label)}\"")
        appendLine("    app:skHint=\"${escapeXml(state.hint)}\"")
        appendLine("    app:skSupportingText=\"${escapeXml(state.supportingText)}\"")
        appendLine("    app:skValue=\"${escapeXml(state.value)}\"")
        appendLine("    app:skRequired=\"${state.required}\"")
        appendLine("    app:skReadOnly=\"${state.readOnly}\"")
        appendLine("    app:skSingleLine=\"${state.singleLine}\"")
        appendLine("    app:skImeAction=\"${imeAttr(state.imeAction)}\"")
        appendLine("    app:skKeyboardType=\"${keyboardAttr(state.keyboardType)}\" />")
    }

    private fun escapeXml(value: String): String =
        value
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")

    private fun camel(enumName: String): String =
        enumName.replaceFirstChar { it.lowercase() }

    private fun overflowAttr(overflow: SKTextOverflow): String = when (overflow) {
        SKTextOverflow.Clip -> "clip"
        SKTextOverflow.Ellipsis -> "ellipsis"
        SKTextOverflow.Visible -> "visible"
    }

    private fun alignAttr(align: SKTextAlign): String = when (align) {
        SKTextAlign.Start -> "start"
        SKTextAlign.Center -> "center"
        SKTextAlign.End -> "end"
        SKTextAlign.Justify -> "justify"
    }

    private fun imeAttr(action: SKImeAction): String = when (action) {
        SKImeAction.Default -> "defaultAction"
        SKImeAction.Done -> "done"
        SKImeAction.Go -> "go"
        SKImeAction.Next -> "next"
        SKImeAction.Previous -> "previous"
        SKImeAction.Search -> "search"
        SKImeAction.Send -> "send"
        SKImeAction.None -> "none"
    }

    private fun keyboardAttr(type: SKKeyboardType): String = when (type) {
        SKKeyboardType.Text -> "text"
        SKKeyboardType.Ascii -> "ascii"
        SKKeyboardType.Number -> "number"
        SKKeyboardType.Phone -> "phone"
        SKKeyboardType.Email -> "email"
        SKKeyboardType.Password -> "password"
        SKKeyboardType.Uri -> "uri"
    }
}
