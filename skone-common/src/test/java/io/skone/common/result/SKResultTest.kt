package io.skone.common.result

import io.skone.common.error.SKError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKResultTest {

    @Test
    fun `success factory wraps value`() {
        val result = SKResult.success(42)
        assertTrue(result is SKResult.Success)
        assertEquals(42, result.getOrNull())
        assertNull(result.errorOrNull())
    }

    @Test
    fun `failure factory wraps error`() {
        val error = SKError("code", "message")
        val result = SKResult.failure<Int>(error)
        assertTrue(result is SKResult.Failure)
        assertNull(result.getOrNull())
        assertEquals(error, result.errorOrNull())
    }

    @Test
    fun `map transforms success and preserves failure`() {
        val mapped = SKResult.success(2).map { it * 3 }
        assertEquals(6, mapped.getOrNull())

        val failure = SKResult.failure<Int>("x", "y")
        assertEquals(failure, failure.map { it * 3 })
    }

    @Test
    fun `flatMap chains success`() {
        val result = SKResult.success(2).flatMap { SKResult.success(it + 1) }
        assertEquals(3, result.getOrNull())
    }

    @Test
    fun `getOrDefault returns fallback on failure`() {
        val result = SKResult.failure<String>("c", "m")
        assertEquals("fallback", result.getOrDefault("fallback"))
    }

    @Test
    fun `onSuccess and onFailure invoke correct branch`() {
        var successCalled = false
        var failureCalled = false

        SKResult.success("ok").onSuccess { successCalled = true }.onFailure { failureCalled = true }
        assertTrue(successCalled)
        assertTrue(!failureCalled)

        successCalled = false
        SKResult.failure<String>("c", "m").onSuccess { successCalled = true }.onFailure { failureCalled = true }
        assertTrue(!successCalled)
        assertTrue(failureCalled)
    }
}
