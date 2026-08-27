package io.skone.compose.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.common.log.SKNoOpLogger
import io.skone.compose.theme.SKTheme
import io.skone.component.framework.SKComponentRuntime
import io.skone.theme.SKThemeMode
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose entry-point hygiene: default calls must not require `@OptIn(SKInternal)`.
 */
@RunWith(AndroidJUnit4::class)
class SKComposePublicApiHygieneTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun skThemeAndRememberRuntimeWorkWithoutInternalDefaultsInCallSite() {
        lateinit var runtime: SKComponentRuntime
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                runtime = rememberSKComponentRuntime()
            }
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertNotNull(runtime.logger)
        }
    }

    @Test
    fun rememberRuntimeAcceptsPublicLogger() {
        lateinit var runtime: SKComponentRuntime
        composeRule.setContent {
            SKTheme {
                runtime = rememberSKComponentRuntime(logger = SKNoOpLogger)
            }
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertSame(SKNoOpLogger, runtime.logger)
        }
    }
}
