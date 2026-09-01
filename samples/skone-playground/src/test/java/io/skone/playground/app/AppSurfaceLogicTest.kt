package io.skone.playground.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AppSurfaceLogicTest {

    private val people = AppSurfaceLogic.sampleDirectory

    @Test
    fun filter_byTabAndUnreadAndQuery() {
        val teamUnread = AppSurfaceLogic.filterDirectory(
            people = people,
            query = "",
            tab = PeopleTab.Team,
            filters = DirectoryFilters(unreadOnly = true),
        )
        assertEquals(listOf("Ada Lovelace"), teamUnread.map { it.name })

        val search = AppSurfaceLogic.filterDirectory(
            people = people,
            query = "hop",
            tab = PeopleTab.All,
            filters = DirectoryFilters(),
        )
        assertEquals(listOf("Grace Hopper"), search.map { it.name })
    }

    @Test
    fun filter_zeroResults() {
        val none = AppSurfaceLogic.filterDirectory(
            people = people,
            query = "zzzz",
            tab = PeopleTab.All,
            filters = DirectoryFilters(),
        )
        assertTrue(none.isEmpty())
    }

    @Test
    fun filter_starredTab() {
        val starred = AppSurfaceLogic.filterDirectory(
            people = people,
            query = "",
            tab = PeopleTab.Starred,
            filters = DirectoryFilters(),
        )
        assertEquals(setOf("Ada Lovelace", "Alan Turing"), starred.map { it.name }.toSet())
    }

    @Test
    fun form_validationGatesSubmit() {
        assertFalse(AppSurfaceLogic.canSubmitProfile(FormFieldSnapshot(displayName = "A", email = "x@y.z")))
        assertFalse(AppSurfaceLogic.canSubmitProfile(FormFieldSnapshot(displayName = "Ada", email = "nope")))
        assertTrue(
            AppSurfaceLogic.canSubmitProfile(
                FormFieldSnapshot(displayName = "Ada", email = "ada@example.com"),
            ),
        )
    }

    @Test
    fun shell_titlesAreStable() {
        assertEquals("Home", AppSurfaceLogic.shellTitle(ShellDestination.Home))
        assertEquals("Activity", AppSurfaceLogic.shellTitle(ShellDestination.Activity))
        assertEquals("Settings", AppSurfaceLogic.shellTitle(ShellDestination.Settings))
    }
}
