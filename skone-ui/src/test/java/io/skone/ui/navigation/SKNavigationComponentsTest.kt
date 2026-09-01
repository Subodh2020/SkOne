package io.skone.ui.navigation

import io.skone.component.framework.icon.SKIconKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKNavigationComponentsTest {

    @Test
    fun navigationBar_selectsWhenEnabled() {
        val items = listOf(
            SKNavigationItem("home", "Home"),
            SKNavigationItem("search", "Search"),
        )
        val bar = SKNavigationBarComponent.create(id = "n1", items = items, selectedId = "home")
        assertEquals("home", bar.selectedId)
        bar.select("search")
        assertEquals("search", bar.selectedId)
        bar.setEnabled(false)
        assertFalse(bar.interactive)
        bar.select("home")
        assertEquals("search", bar.selectedId)
    }

    @Test
    fun topAppBar_storesIcons() {
        val bar = SKTopAppBarComponent.create(
            id = "t1",
            title = "Library",
            navigationIcon = SKIconKey("skone.icon.back", contentDescription = "Back"),
        )
        assertEquals("Library", bar.title)
        assertEquals("Back", bar.navigationIcon?.contentDescription)
        bar.setTitle("")
        assertEquals("", bar.title)
        assertTrue(bar.title.isEmpty())
    }
}
