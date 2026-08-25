package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.SKTheme
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Consumer-facing Compose contracts for [SKTextField]:
 * validation → UI, disabled/readOnly input blocking, runtime optionality.
 *
 * Floating-label layout contracts remain in [SKTextFieldComposeTest].
 *
 * Text input targets the editable semantics node ([hasSetTextAction]), because the
 * outer merged a11y node (testTag / contentDescription) does not expose RequestFocus.
 */
@RunWith(AndroidJUnit4::class)
class SKTextFieldContractComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun requiredRuleSubmitShowsErrorTextAndSemantics() {
        lateinit var form: SKFormController
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var value by remember { mutableStateOf("") }
                    SKTextField(
                        value = value,
                        onValueChange = { value = it },
                        fieldId = "name",
                        label = "Name",
                        required = true,
                        rules = listOf(SKRequiredRule()),
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Name field",
                            testTag = "name_field",
                        ),
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertTrue(form.registry.get("name") != null)
            val result = form.submit()
            assertFalse(result.isValid)
            assertTrue(form.errors.hasErrors())
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Required").assertIsDisplayed()
        composeRule.onNodeWithTag("name_field").assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Error),
        )
    }

    @Test
    fun emailRuleSubmitShowsValidationError() {
        lateinit var form: SKFormController
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var value by remember { mutableStateOf("") }
                    SKTextField(
                        value = value,
                        onValueChange = { value = it },
                        fieldId = "email",
                        label = "Email",
                        rules = listOf(SKEmailRule()),
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Email field",
                            testTag = "email_field",
                        ),
                    )
                }
            }
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("not-an-email")
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertTrue(
                form.registry.state("email")?.displayValue?.contains("not-an-email") == true,
            )
            val result = form.submit()
            assertFalse(result.isValid)
            assertTrue(form.errors.errorsFor("email").isNotEmpty())
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Invalid email").assertIsDisplayed()
        composeRule.onNodeWithTag("email_field").assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Error),
        )
    }

    @Test
    fun disabledFieldRejectsTextInput() {
        val valueState = mutableStateOf("keep-me")
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by valueState
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "disabled",
                    label = "Email",
                    enabled = false,
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Disabled email",
                        testTag = "disabled_field",
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("disabled_field").assertIsDisplayed()
        composeRule.onNodeWithText("keep-me").assertIsDisplayed()
        // Disabled BasicTextField does not expose SetText — there is no editable target.
        assertEquals(
            0,
            composeRule.onAllNodes(hasSetTextAction(), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .size,
        )
        val inputAttempt = runCatching {
            composeRule.onNodeWithTag("disabled_field").performTextInput("hacked")
        }
        assertTrue(inputAttempt.isFailure)
        composeRule.runOnIdle {
            assertEquals("keep-me", valueState.value)
        }
        composeRule.onNodeWithText("hacked").assertDoesNotExist()
    }

    @Test
    fun readOnlyFieldPreservesValueAndRejectsEdit() {
        val valueState = mutableStateOf("readonly@skone.io")
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by valueState
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "readonly",
                    label = "Email",
                    readOnly = true,
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Read-only email",
                        testTag = "readonly_field",
                    ),
                )
            }
        }

        composeRule.onNodeWithText("readonly@skone.io").assertIsDisplayed()
        // readOnly fields may still expose SetText; production must ignore edits.
        runCatching {
            composeRule.onNode(hasSetTextAction()).performTextInput("changed")
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals("readonly@skone.io", valueState.value)
        }
        composeRule.onNodeWithText("readonly@skone.io").assertIsDisplayed()
        composeRule.onNodeWithText("changed").assertDoesNotExist()
    }

    @Test
    fun rendersAndAcceptsInputWithoutRuntime() {
        val valueState = mutableStateOf("")
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                // No ProvideSKComponentRuntime — widgets must still work.
                var value by valueState
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "standalone",
                    label = "Notes",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Notes field",
                        testTag = "notes_field",
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Notes").assertIsDisplayed()
        composeRule.onNode(hasSetTextAction()).performTextInput("hello")
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals("hello", valueState.value)
        }
        composeRule.onNodeWithText("hello").assertIsDisplayed()
    }
}
