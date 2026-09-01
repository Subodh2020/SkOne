package io.skone.ui.layout

import io.skone.component.framework.icon.SKIconKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKContentComponentsTest {

    @Test
    fun listItem_clickableGatesInteraction() {
        val item = SKListItemComponent.create(
            id = "li1",
            headline = "Inbox",
            supportingText = "3 unread",
            clickable = false,
        )
        assertFalse(item.interactive)
        item.performClick()
        item.setClickable(true)
        assertTrue(item.interactive)
        item.setSelected(true)
        assertTrue(item.selected)
        item.setEnabled(false)
        assertFalse(item.interactive)
    }

    @Test
    fun listItem_longHeadlineStored() {
        val long = "A".repeat(200)
        val item = SKListItemComponent.create(id = "li2", headline = long, trailingText = "Now")
        assertEquals(long, item.headline)
        assertEquals("Now", item.trailingText)
        item.setLeadingIcon(SKIconKey("skone.icon.mail", contentDescription = "Mail"))
        assertEquals("Mail", item.leadingIcon?.contentDescription)
    }

    @Test
    fun sectionHeader_actionRequiresLabel() {
        val header = SKSectionHeaderComponent.create(id = "sh1", title = "Today", actionLabel = null)
        header.performAction()
        header.setActionLabel("See all")
        header.performAction()
        assertEquals("See all", header.actionLabel)
    }

    @Test
    fun scaffold_create() {
        val scaffold = SKScaffoldComponent.create(id = "sc1")
        assertEquals(SKScaffoldComponent.COMPONENT_TYPE, scaffold.componentType)
    }
}
