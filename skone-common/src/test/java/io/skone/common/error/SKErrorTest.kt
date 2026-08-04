package io.skone.common.error

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SKErrorTest {

    @Test
    fun `unknown factory uses unknown code`() {
        val error = SKError.unknown("boom")
        assertEquals(SKError.CODE_UNKNOWN, error.code)
        assertEquals("boom", error.message)
        assertNull(error.cause)
        assertTrue(error.metadata.isEmpty())
    }

    @Test
    fun `metadata is retained`() {
        val error = SKError(
            code = "skone.test",
            message = "msg",
            metadata = mapOf("key" to "value"),
        )
        assertEquals("value", error.metadata["key"])
    }

    private fun assertTrue(condition: Boolean) {
        org.junit.jupiter.api.Assertions.assertTrue(condition)
    }
}
