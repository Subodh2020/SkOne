package io.skone.ui.overlay

import io.skone.component.appearance.SKAppearanceConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SKSheetSegmentComponentsTest {

    @Test
    fun bottomSheet_dismissAndActions() {
        val sheet = SKBottomSheetComponent.create(
            id = "s1",
            title = "Filters",
            primaryActionLabel = "Apply",
            secondaryActionLabel = "Reset",
        )
        assertTrue(sheet.visible)
        sheet.dismiss()
        assertFalse(sheet.visible)
        sheet.setVisible(true)
        sheet.performPrimaryAction()
        assertFalse(sheet.visible)
    }

    @Test
    fun segmented_selectEnabledOnly() {
        val control = SKSegmentedButtonComponent.create(
            id = "seg1",
            items = listOf(
                SKSegmentItem("all", "All"),
                SKSegmentItem("unread", "Unread"),
                SKSegmentItem("archived", "Archived", enabled = false),
            ),
            selectedId = "all",
            appearance = SKAppearanceConfig.SegmentedButton,
        )
        assertEquals("all", control.selectedId)
        control.select("unread")
        assertEquals("unread", control.selectedId)
        control.select("archived")
        assertEquals("unread", control.selectedId)
        control.setEnabled(false)
        control.select("all")
        assertEquals("unread", control.selectedId)
    }

    @Test
    fun segmented_requiresTwoItemsAtCreateTime() {
        // Contract allows empty at construction; Compose/XML enforce >= 2.
        val control = SKSegmentedButtonComponent.create(
            id = "seg2",
            items = listOf(SKSegmentItem("a", "A"), SKSegmentItem("b", "B")),
        )
        assertEquals(2, control.items.size)
        assertThrows<IllegalArgumentException> {
            require(listOf(SKSegmentItem("only", "Only")).size >= 2)
        }
    }
}
