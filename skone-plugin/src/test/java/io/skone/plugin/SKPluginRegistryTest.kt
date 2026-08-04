package io.skone.plugin

import io.skone.common.log.SKLog
import io.skone.common.log.SKNoOpLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SKPluginRegistryTest {

    private lateinit var registry: SKPluginRegistry
    private val context = SKDefaultPluginContext(SKNoOpLogger)

    @BeforeEach
    fun setUp() {
        SKLog.install(SKNoOpLogger)
        registry = SKInMemoryPluginRegistry { context }
    }

    @AfterEach
    fun tearDown() {
        SKLog.resetForTest()
    }

    @Test
    fun `register attaches and stores plugin`() {
        val plugin = TrackingPlugin("demo")
        registry.register(plugin)

        assertEquals(1, plugin.attachCount)
        assertEquals(0, plugin.detachCount)
        assertEquals(plugin, registry.get("demo"))
        assertEquals(1, registry.all().size)
    }

    @Test
    fun `register same id replaces and detaches previous`() {
        val first = TrackingPlugin("demo", version = "1.0.0")
        val second = TrackingPlugin("demo", version = "2.0.0")

        registry.register(first)
        registry.register(second)

        assertEquals(1, first.attachCount)
        assertEquals(1, first.detachCount)
        assertEquals(1, second.attachCount)
        assertEquals(second, registry.get("demo"))
        assertEquals("2.0.0", registry.get("demo")?.version)
    }

    @Test
    fun `unregister detaches and removes plugin`() {
        val plugin = TrackingPlugin("demo")
        registry.register(plugin)
        registry.unregister("demo")

        assertEquals(1, plugin.detachCount)
        assertNull(registry.get("demo"))
        assertTrue(registry.all().isEmpty())
    }

    @Test
    fun `unregister unknown id is no-op`() {
        registry.unregister("missing")
        assertTrue(registry.all().isEmpty())
    }

    private class TrackingPlugin(
        override val id: String,
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
