package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.theme.SKTheme
import io.skone.forms.SKFormController
import io.skone.compose.forms.ProvideSKFormController
import io.skone.theme.SKThemeMode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Accessibility-focused Compose tests for [SKTextField].
 */
@RunWith(AndroidJUnit4::class)
class SKTextFieldAccessibilityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exposesContentDescriptionAndTestTag() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "a11y_name",
                    label = "Full name",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Full name field",
                        testTag = "sktextfield_fullname",
                    ),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Full name field").assertIsDisplayed()
        composeRule.onNodeWithTag("sktextfield_fullname").assertIsDisplayed()
    }

    @Test
    fun errorStateSetsSemanticsError() {
        lateinit var form: SKFormController
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var value by remember { mutableStateOf("x") }
                    SKTextField(
                        value = value,
                        onValueChange = { value = it },
                        fieldId = "email",
                        label = "Email",
                        supportingText = "Help",
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Email field",
                            testTag = "sktextfield_email",
                        ),
                    )
                }
            }
        }
        composeRule.runOnIdle {
            form.errors.setFieldErrors(
                "email",
                io.skone.component.validation.SKValidationResult.Invalid(
                    io.skone.component.validation.SKValidationError("email", "Invalid email"),
                ),
            )
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("sktextfield_email").assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Error),
        )
    }
}
