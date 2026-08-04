package io.skone.ui.text

import io.skone.component.appearance.SKAppearanceConfig
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKTextComponentTest {

    @Test
    fun `create sets plain text and defaults`() {
        val component = SKTextComponent.create(id = "t1", text = "Hello")
        assertEquals("Hello", component.text)
        assertEquals(SKTextOverflow.Clip, component.overflow)
        assertEquals(SKAppearanceConfig.Text.contentColorRole, component.config.appearance.contentColorRole)
        assertEquals(SKTypographyRole.BodyLarge, component.config.appearance.typographyRole)
    }

    @Test
    fun `annotated text and spans are retained`() {
        val annotated = SKAnnotatedText(
            text = "Hello World",
            spans = listOf(
                SKTextSpan(0, 5, listOf(SKSpanStyle.Bold)),
                SKTextSpan(6, 11, listOf(SKSpanStyle.ColorRole(SKColorRole.Primary))),
            ),
        )
        val component = SKTextComponent.create(id = "t2", annotated = annotated)
        assertEquals(2, component.annotated.spans.size)
        assertEquals(SKSpanStyle.Bold, component.annotated.spans[0].styles.first())
    }

    @Test
    fun `setters update mutable fields`() {
        val component = SKTextComponent.create(id = "t3", text = "A")
        component.setText("B")
        component.setOverflow(SKTextOverflow.Ellipsis)
        component.setMaxLines(2)
        component.setTextAlign(SKTextAlign.Center)
        component.setSoftWrap(false)

        assertEquals("B", component.text)
        assertEquals(SKTextOverflow.Ellipsis, component.overflow)
        assertEquals(2, component.maxLines)
        assertEquals(SKTextAlign.Center, component.textAlign)
        assertFalse(component.softWrap)
    }

    @Test
    fun `passive by default unless clickable`() {
        val passive = SKTextComponent.create(id = "t4", text = "x")
        assertFalse(passive.config.behavior.clickable)

        val clickable = SKTextComponent.create(id = "t5", text = "x", clickable = true)
        assertTrue(clickable.config.behavior.clickable)
    }
}
