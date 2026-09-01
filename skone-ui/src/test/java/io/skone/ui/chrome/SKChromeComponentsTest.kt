package io.skone.ui.chrome

import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKChromeComponentsTest {

    @Test
    fun tabRow_selectsEnabledOnly() {
        val row = SKTabRowComponent.create(
            id = "t1",
            items = listOf(
                SKTabItem("inbox", "Inbox"),
                SKTabItem("spam", "Spam", enabled = false),
                SKTabItem("sent", "Sent"),
            ),
            selectedId = "inbox",
        )
        assertEquals("inbox", row.selectedId)
        row.select("sent")
        assertEquals("sent", row.selectedId)
        row.select("spam")
        assertEquals("sent", row.selectedId)
        row.setEnabled(false)
        assertFalse(row.interactive)
        row.select("inbox")
        assertEquals("sent", row.selectedId)
    }

    @Test
    fun badge_semanticLabel_dotSilentUnlessExplicitCd() {
        val count = SKBadgeComponent.create(id = "b1", text = "12")
        assertEquals("12", count.semanticLabel)
        val dot = SKBadgeComponent.create(id = "b2", dot = true)
        assertNull(dot.semanticLabel)
        val labeledDot = SKBadgeComponent.create(
            id = "b3",
            dot = true,
            accessibility = SKAccessibilityConfig(contentDescription = "Unread"),
        )
        assertEquals("Unread", labeledDot.semanticLabel)
        val hidden = SKBadgeComponent.create(id = "b4", text = "9", visible = false)
        assertNull(hidden.semanticLabel)
    }

    @Test
    fun avatar_semanticDescription_initialsFallback() {
        val avatar = SKAvatarComponent.create(
            id = "a1",
            initials = "SK",
            appearance = SKAppearanceConfig.Avatar,
        )
        assertEquals("SK", avatar.semanticDescription)
        val withCd = SKAvatarComponent.create(
            id = "a2",
            initials = "SK",
            accessibility = SKAccessibilityConfig(contentDescription = "Subodh"),
        )
        assertEquals("Subodh", withCd.semanticDescription)
    }
}
