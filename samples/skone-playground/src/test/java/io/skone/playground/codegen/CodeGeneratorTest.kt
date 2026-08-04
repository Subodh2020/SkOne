package io.skone.playground.codegen

import io.skone.playground.editor.SkTextEditorState
import io.skone.playground.editor.SkTextFieldEditorState
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CodeGeneratorTest {

    @Test
    fun composeSkText_containsRolesAndText() {
        val code = ComposeCodeGenerator.skText(
            SkTextEditorState(
                text = "Hello",
                typographyRole = SKTypographyRole.TitleMedium,
                contentColorRole = SKColorRole.Primary,
            ),
        )
        assertTrue(code.contains("SKText("))
        assertTrue(code.contains("Hello"))
        assertTrue(code.contains("SKTypographyRole.TitleMedium"))
        assertTrue(code.contains("SKColorRole.Primary"))
    }

    @Test
    fun xmlSkTextField_containsAttrs() {
        val code = XmlCodeGenerator.skTextField(
            SkTextFieldEditorState(
                label = "Email",
                required = true,
                imeAction = SKImeAction.Next,
                keyboardType = SKKeyboardType.Email,
            ),
        )
        assertTrue(code.contains("SKTextFieldView"))
        assertTrue(code.contains("app:skLabel=\"Email\""))
        assertTrue(code.contains("app:skRequired=\"true\""))
        assertTrue(code.contains("app:skImeAction=\"next\""))
        assertTrue(code.contains("app:skKeyboardType=\"email\""))
    }

    @Test
    fun composeSkTextField_escapesQuotes() {
        val code = ComposeCodeGenerator.skTextField(
            SkTextFieldEditorState(label = "Say \"hi\""),
        )
        assertTrue(code.contains("Say \\\"hi\\\""))
    }
}
