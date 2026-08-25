@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.SKAnalyticsConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.analytics.SKRecordingAnalyticsHook
import io.skone.compose.theme.SKTheme
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.theme.SKThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose contracts for [rememberSKComponentRuntime] / [ProvideSKComponentRuntime].
 */
@RunWith(AndroidJUnit4::class)
class SKComponentRuntimeComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun widgetsRenderWithoutRuntime() {
        var fieldValue by mutableStateOf("")
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                // No ProvideSKComponentRuntime.
                SKText(text = "No runtime text")
                SKTextField(
                    value = fieldValue,
                    onValueChange = { fieldValue = it },
                    fieldId = "notes",
                    label = "Notes",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Notes field",
                        testTag = "notes_field",
                    ),
                )
            }
        }
        composeRule.onNodeWithText("No runtime text").assertIsDisplayed()
        composeRule.onNodeWithText("Notes").assertIsDisplayed()
        composeRule.onNodeWithTag("notes_field").assertIsDisplayed()
    }

    @Test
    fun provideRuntimeAttachesWidgetAndTracksAnalytics() {
        val hook = SKRecordingAnalyticsHook()
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                val runtime = rememberSKComponentRuntime(analytics = hook)
                ProvideSKComponentRuntime(runtime) {
                    SKText(
                        text = "Tracked",
                        analytics = SKAnalyticsConfig(
                            enabled = true,
                            componentName = "SKText",
                        ),
                        onClick = {},
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Tracked text",
                            testTag = "tracked_text",
                        ),
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertTrue(
                "Attach should emit analytics when runtime is provided",
                hook.entries.any { it.name.contains("Attached") },
            )
        }

        composeRule.onNodeWithTag("tracked_text").performClick()
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertTrue(
                "Click should emit analytics through the provided runtime",
                hook.entries.any { it.name.contains("Clicked") },
            )
        }
    }

    @Test
    fun rememberSKComponentRuntimeReturnsStableInstanceAcrossRecomposition() {
        val seen = mutableListOf<SKComponentRuntime>()
        var tick by mutableIntStateOf(0)
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                val runtime = rememberSKComponentRuntime()
                seen += runtime
                // Read tick so recomposition runs when it changes.
                SKText(text = "tick=$tick")
            }
        }
        composeRule.onNodeWithText("tick=0").assertIsDisplayed()
        composeRule.runOnIdle { tick = 1 }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("tick=1").assertIsDisplayed()
        composeRule.runOnIdle {
            assertTrue("Expected at least two compositions", seen.size >= 2)
            assertSame(seen.first(), seen.last())
            assertEquals(1, seen.distinct().size)
        }
    }
}
