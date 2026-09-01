package io.skone.consumer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsumerLogicTest {
    @Test
    fun filter_byQueryAndTeamTab() {
        val rows = ConsumerLogic.filter(
            query = "ada",
            tab = PeopleTab.Team,
            filters = Filters(),
        )
        assertEquals(1, rows.size)
        assertEquals("1", rows.first().id)
    }

    @Test
    fun filter_unreadOnly() {
        val rows = ConsumerLogic.filter(
            query = "",
            tab = PeopleTab.All,
            filters = Filters(unreadOnly = true),
        )
        assertTrue(rows.all { it.unread })
        assertEquals(2, rows.size)
    }

    @Test
    fun canSubmit_requiresNameAndEmailShape() {
        assertFalse(ConsumerLogic.canSubmit(ProfileDraft("A", "a@b.com")))
        assertFalse(ConsumerLogic.canSubmit(ProfileDraft("Ada", "not-an-email")))
        assertTrue(ConsumerLogic.canSubmit(ProfileDraft("Ada", "ada@example.com")))
    }
}
