@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone

import io.skone.ai.SKAIConfig
import io.skone.ai.SKAIProvider
import io.skone.ai.SKAIRequest
import io.skone.ai.SKAIResponse
import io.skone.common.error.SKError
import io.skone.common.log.SKNoOpLogger
import io.skone.common.result.SKResult
import io.skone.common.result.getOrNull
import io.skone.plugin.SKPlugin
import io.skone.plugin.SKPluginContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SKOneTest {

    @AfterEach
    fun tearDown() {
        SKOne.resetForTest()
    }

    @Test
    fun `initialize marks sdk ready and registers plugins`() {
        val plugin = TrackingPlugin()
        SKOne.initialize(
            SKOneConfig(
                logger = SKNoOpLogger,
                plugins = listOf(plugin),
            ),
        )

        assertTrue(SKOne.isInitialized())
        assertEquals(1, plugin.attachCount)
        assertEquals(plugin, SKOne.plugins().get("demo"))
        assertEquals(SKNoOpLogger, SKOne.logger())
    }

    @Test
    fun `double initialize replaces configuration with new plugins`() {
        val first = TrackingPlugin(id = "first")
        val second = TrackingPlugin(id = "second")

        SKOne.initialize(SKOneConfig(logger = SKNoOpLogger, plugins = listOf(first)))
        SKOne.initialize(SKOneConfig(logger = SKNoOpLogger, plugins = listOf(second)))

        assertEquals(1, first.detachCount)
        assertNull(SKOne.plugins().get("first"))
        assertEquals(second, SKOne.plugins().get("second"))
    }

    @Test
    fun `plugins and logger throw before initialize`() {
        assertThrows<IllegalStateException> { SKOne.plugins() }
        assertThrows<IllegalStateException> { SKOne.logger() }
        assertFalse(SKOne.isInitialized())
    }

    @Test
    fun `aiComplete fails clearly when ai disabled`() = runTest {
        SKOne.initialize(SKOneConfig(logger = SKNoOpLogger, ai = SKAIConfig.Disabled))

        val result = SKOne.aiComplete(SKAIRequest(prompt = "hello"))
        assertTrue(result is SKResult.Failure)
        assertEquals(SKError.CODE_AI_UNAVAILABLE, (result as SKResult.Failure).error.code)
    }

    @Test
    fun `aiComplete uses registered provider`() = runTest {
        val provider = object : SKAIProvider {
            override val id: String = "test"
            override suspend fun complete(request: SKAIRequest): SKResult<SKAIResponse> =
                SKResult.success(SKAIResponse(text = "echo:${request.prompt}", providerId = id))
        }

        SKOne.initialize(
            SKOneConfig(
                logger = SKNoOpLogger,
                ai = SKAIConfig(defaultProviderId = "test", providers = listOf(provider)),
            ),
        )

        val result = SKOne.aiComplete(SKAIRequest(prompt = "hi"))
        assertEquals("echo:hi", result.getOrNull()?.text)
    }

    private fun assertNull(value: Any?) {
        org.junit.jupiter.api.Assertions.assertNull(value)
    }

    private class TrackingPlugin(
        override val id: String = "demo",
        override val version: String = "1.0.0",
    ) : SKPlugin {
        var attachCount: Int = 0
        var detachCount: Int = 0

        override fun onAttach(context: SKPluginContext) {
            attachCount++
        }

        override fun onDetach() {
            detachCount++
        }
    }
}
