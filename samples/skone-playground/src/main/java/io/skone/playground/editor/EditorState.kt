package io.skone.playground.editor

import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import io.skone.ui.text.SKTextAlign
import io.skone.ui.text.SKTextOverflow

/**
 * Editable properties for the SKText live editor.
 */
data class SkTextEditorState(
    val text: String = "Hello SKOne",
    val typographyRole: SKTypographyRole = SKTypographyRole.BodyLarge,
    val contentColorRole: SKColorRole = SKColorRole.OnSurface,
    val maxLines: Int = 3,
    val softWrap: Boolean = true,
    val overflow: SKTextOverflow = SKTextOverflow.Ellipsis,
    val textAlign: SKTextAlign = SKTextAlign.Start,
)

/**
 * Editable properties for the SKTextField live editor.
 */
data class SkTextFieldEditorState(
    val value: String = "",
    val label: String = "Email",
    val hint: String = "name@company.com",
    val supportingText: String = "We'll never share your email.",
    val required: Boolean = true,
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val singleLine: Boolean = true,
    val imeAction: SKImeAction = SKImeAction.Next,
    val keyboardType: SKKeyboardType = SKKeyboardType.Email,
)
