package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.component.ProvideSKComponentRuntime
import io.skone.compose.component.rememberSKComponentRuntime
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.SKTheme
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemeMode
import io.skone.ui.field.SKImeAction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI + accessibility tests for [SKTextField].
 */
@RunWith(AndroidJUnit4::class)
class SKTextFieldComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyUnfocusedShowsLabelNotHint() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                val runtime = rememberSKComponentRuntime()
                ProvideSKComponentRuntime(runtime) {
                    var value by remember { mutableStateOf("") }
                    SKTextField(
                        value = value,
                        onValueChange = { value = it },
                        fieldId = "email",
                        label = "Email",
                        hint = "name@company.com",
                    )
                }
            }
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("name@company.com").assertDoesNotExist()
    }

    @Test
    fun focusedEmptyShowsFloatingLabelAndHint() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "email",
                    label = "Email",
                    hint = "name@company.com",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Email field",
                        testTag = "email_field",
                    ),
                )
            }
        }
        composeRule.onNodeWithTag("email_field").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("name@company.com").assertIsDisplayed()
    }

    @Test
    fun valueEnteredHidesHintAndKeepsFloatingLabel() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("user@skone.io") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "email",
                    label = "Email",
                    hint = "name@company.com",
                )
            }
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("user@skone.io").assertIsDisplayed()
        composeRule.onNodeWithText("name@company.com").assertDoesNotExist()
    }

    @Test
    fun disabledFieldShowsLabel() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKTextField(
                    value = "",
                    onValueChange = {},
                    fieldId = "disabled",
                    label = "Email",
                    hint = "name@company.com",
                    enabled = false,
                )
            }
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun readOnlyFieldShowsValue() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKTextField(
                    value = "readonly@skone.io",
                    onValueChange = {},
                    fieldId = "readonly",
                    label = "Email",
                    readOnly = true,
                )
            }
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("readonly@skone.io").assertIsDisplayed()
    }

    @Test
    fun showsSupportingText() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKTextField(
                    value = "",
                    onValueChange = {},
                    fieldId = "support",
                    label = "Email",
                    supportingText = "We'll never share your email.",
                )
            }
        }
        composeRule.onNodeWithText("We'll never share your email.").assertIsDisplayed()
    }

    @Test
    fun multiLineFieldShowsLabel() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKTextField(
                    value = "Line one\nLine two",
                    onValueChange = {},
                    fieldId = "notes",
                    label = "Notes",
                    singleLine = false,
                )
            }
        }
        composeRule.onNodeWithText("Notes").assertIsDisplayed()
        composeRule.onNodeWithText("Line one\nLine two").assertIsDisplayed()
    }

    @Test
    fun appliesContentDescriptionAndTestTag() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "a11y",
                    label = "Name",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Full name field",
                        testTag = "sktextfield_name",
                    ),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Full name field").assertIsDisplayed()
        composeRule.onNodeWithTag("sktextfield_name").assertIsDisplayed()
    }

    @Test
    fun errorStateKeepsFloatingLabel() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKTextField(
                    value = "bad",
                    onValueChange = {},
                    fieldId = "email",
                    label = "Email",
                    supportingText = "Please enter a valid email address",
                    accessibility = SKAccessibilityConfig(testTag = "email_error"),
                )
            }
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Please enter a valid email address").assertIsDisplayed()
    }

    @Test
    fun registersWithFormAndAcceptsInput() {
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
                        required = true,
                        rules = listOf(SKRequiredRule()),
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "email_register"),
                    )
                }
            }
        }
        composeRule.onNodeWithText("Email *").assertIsDisplayed()
        composeRule.onNodeWithTag("email_register").performTextInput("user@skone.io")
        composeRule.runOnIdle {
            assert(form.registry.get("email") != null)
            assert(form.registry.state("email")?.displayValue?.contains("user") == true)
        }
    }
}
