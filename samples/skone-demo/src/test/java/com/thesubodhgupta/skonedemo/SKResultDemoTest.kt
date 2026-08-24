package com.thesubodhgupta.skonedemo

import io.skone.common.result.SKResult
import org.junit.Assert.assertEquals
import org.junit.Test

class SKResultDemoTest {
    @Test
    fun success_returnsValue() {
        val result = SKResult.success("Maven Central")
        assertEquals("Maven Central", (result as SKResult.Success).value)
    }
}
