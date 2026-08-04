package io.skone.playground.catalog

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlaygroundCatalogTest {

    @Test
    fun widgets_areRegistered() {
        val widgets = PlaygroundCatalog.widgets()
        assertTrue(widgets.any { it.id == CatalogId.SkText })
        assertTrue(widgets.any { it.id == CatalogId.SkTextField })
    }

    @Test
    fun search_matchesTagsAndTitle() {
        assertTrue(PlaygroundCatalog.search("textfield").any { it.id == CatalogId.SkTextField })
        assertTrue(PlaygroundCatalog.search("theme").any { it.id == CatalogId.DesignSystem })
        assertTrue(PlaygroundCatalog.search("xml").isNotEmpty())
    }

    @Test
    fun emptySearch_returnsAll() {
        assertEquals(PlaygroundCatalog.entries.size, PlaygroundCatalog.search("").size)
    }
}
