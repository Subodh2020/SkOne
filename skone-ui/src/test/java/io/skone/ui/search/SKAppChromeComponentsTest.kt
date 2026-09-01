package io.skone.ui.search

import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.icon.SKIconKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKAppChromeComponentsTest {

    @Test
    fun searchBar_queryClearAndDisabled() {
        val bar = SKSearchBarComponent.create(id = "s1", query = "hello")
        assertEquals("hello", bar.query)
        assertTrue(bar.clearVisible)
        bar.setQuery("")
        assertFalse(bar.clearVisible)
        bar.setEnabled(false)
        assertFalse(bar.interactive)
        bar.setQuery("x")
        bar.clear()
        assertEquals("x", bar.query)
    }

    @Test
    fun emptyState_actionsRequireLabels() {
        val empty = SKEmptyStateComponent.create(
            id = "e1",
            title = "No results",
            description = "Try another query",
            primaryActionLabel = "Clear filters",
        )
        assertEquals("No results", empty.title)
        var clicks = 0
        empty.addInteractionListener(
            object : io.skone.component.framework.SKInteractionListener {
                override fun onClick(component: io.skone.component.framework.SKInteractiveComponent) {
                    clicks++
                }
            },
        )
        empty.performPrimaryAction()
        assertEquals(1, clicks)
        empty.performSecondaryAction()
        assertEquals(1, clicks)
    }

    @Test
    fun fab_semanticDescriptionAndDisabled() {
        val missing = SKFabComponent.create(id = "f1", icon = SKIconKey("skone.icon.add"))
        assertNull(missing.semanticDescription)

        val fab = SKFabComponent.create(
            id = "f2",
            icon = SKIconKey("skone.icon.add", contentDescription = "Add item"),
            appearance = SKAppearanceConfig.Fab,
            accessibility = SKAccessibilityConfig(testTag = "fab"),
        )
        assertEquals("Add item", fab.semanticDescription)
        fab.setEnabled(false)
        assertFalse(fab.interactive)
        var clicked = false
        fab.addInteractionListener(
            object : io.skone.component.framework.SKInteractionListener {
                override fun onClick(component: io.skone.component.framework.SKInteractiveComponent) {
                    clicked = true
                }
            },
        )
        fab.performClick()
        assertFalse(clicked)
    }
}
