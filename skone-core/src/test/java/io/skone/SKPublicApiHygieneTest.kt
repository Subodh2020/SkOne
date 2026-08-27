package io.skone

import io.skone.common.annotation.SKInternal
import io.skone.common.log.SKLogger
import io.skone.common.log.SKNoOpLogger
import io.skone.component.framework.SKComponentRuntime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Public API hygiene: flagship config/runtime entry points must be usable without
 * file-level `@OptIn(SKInternal)` and must not require naming `@SKInternal` default types.
 *
 * Note: [SKOne.initialize] with the library default logger needs Android Log; JVM unit tests
 * use [SKNoOpLogger] for initialize while still constructing bare [SKOneConfig].
 */
class SKPublicApiHygieneTest {

    @AfterEach
    @OptIn(SKInternal::class)
    fun tearDown() {
        SKOne.resetForTest()
    }

    @Test
    fun skOneConfigDefaultConstructibleWithoutNamingInternalLogger() {
        val config = SKOneConfig()
        assertNotNull(config.logger)
    }

    @Test
    fun skOneInitializeAcceptsConfigWithoutInternalTypeNames() {
        SKOne.initialize(SKOneConfig(logger = SKNoOpLogger))
        assertTrue(SKOne.isInitialized())
    }

    @Test
    fun skOneConfigAcceptsPublicCustomLogger() {
        val config = SKOneConfig(logger = SKNoOpLogger)
        assertSame(SKNoOpLogger, config.logger)
    }

    @Test
    fun componentRuntimeCreateUsesDefaultLoggerWhenOmitted() {
        val runtime = SKComponentRuntime.create()
        assertNotNull(runtime.logger)
    }

    @Test
    fun componentRuntimeCreateAcceptsPublicLogger() {
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        assertSame(SKNoOpLogger as SKLogger, runtime.logger)
    }
}
