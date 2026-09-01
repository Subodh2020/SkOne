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
        assertTrue(widgets.any { it.id == CatalogId.SkButton })
        assertTrue(widgets.any { it.id == CatalogId.SkCheckbox })
        assertTrue(widgets.any { it.id == CatalogId.SkSwitch })
        assertTrue(widgets.any { it.id == CatalogId.SkIconButton })
        assertTrue(widgets.any { it.id == CatalogId.SkRadioGroup })
        assertTrue(widgets.any { it.id == CatalogId.SkChip })
        assertTrue(widgets.any { it.id == CatalogId.SkSlider })
        assertTrue(widgets.any { it.id == CatalogId.SkProgressIndicator })
        assertTrue(widgets.any { it.id == CatalogId.SkDivider })
        assertTrue(widgets.any { it.id == CatalogId.SkCard })
        assertTrue(widgets.any { it.id == CatalogId.SkSnackbar })
        assertTrue(widgets.any { it.id == CatalogId.SkAlertDialog })
        assertTrue(widgets.any { it.id == CatalogId.SkTopAppBar })
        assertTrue(widgets.any { it.id == CatalogId.SkNavigationBar })
        assertTrue(widgets.any { it.id == CatalogId.SkListItem })
        assertTrue(widgets.any { it.id == CatalogId.SkSectionHeader })
        assertTrue(widgets.any { it.id == CatalogId.SkScaffold })
        assertTrue(widgets.any { it.id == CatalogId.SkSearchBar })
        assertTrue(widgets.any { it.id == CatalogId.SkEmptyState })
        assertTrue(widgets.any { it.id == CatalogId.SkFab })
        assertTrue(widgets.any { it.id == CatalogId.SkTabRow })
        assertTrue(widgets.any { it.id == CatalogId.SkBadge })
        assertTrue(widgets.any { it.id == CatalogId.SkAvatar })
        assertTrue(widgets.any { it.id == CatalogId.SkMenu })
        assertTrue(widgets.any { it.id == CatalogId.SkDropdownMenu })
        assertTrue(widgets.any { it.id == CatalogId.SkTooltip })
        assertTrue(widgets.any { it.id == CatalogId.SkBottomAppBar })
        assertTrue(widgets.any { it.id == CatalogId.SkBottomSheet })
        assertTrue(widgets.any { it.id == CatalogId.SkSegmentedButton })
    }

    @Test
    fun applicationExamples_areRegistered() {
        val samples = PlaygroundCatalog.samples()
        assertTrue(samples.any { it.id == CatalogId.AppListFilter })
        assertTrue(samples.any { it.id == CatalogId.AppFormValidation })
        assertTrue(samples.any { it.id == CatalogId.AppShellNav })
        assertTrue(PlaygroundCatalog.search("filter").any { it.id == CatalogId.AppListFilter })
        assertTrue(PlaygroundCatalog.search("validation").any { it.id == CatalogId.AppFormValidation })
        assertTrue(PlaygroundCatalog.search("shell").any { it.id == CatalogId.AppShellNav })
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
