package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.SKTheme
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Accessibility-focused Compose tests for [SKTextField].
 *
 * Primary editable node owns contentDescription, testTag, Error, and SetText.
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
                        testTag = "sktextfield_name",
                    ),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Full name field").assertIsDisplayed()
        composeRule.onNodeWithTag("sktextfield_name").assertIsDisplayed()
    }

    @Test
    fun testTagAndSetTextAreOnSamePrimaryNode() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "primary",
                    label = "Email",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Email field",
                        testTag = "email_primary",
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("email_primary")
            .assert(hasSetTextAction())
            .assertIsDisplayed()

        // Same tagged node accepts text input (no ancestor/unmerged workaround).
        composeRule.onNodeWithTag("email_primary").performTextInput("a@b.co")
        composeRule.waitForIdle()
        composeRule.onNodeWithText("a@b.co").assertIsDisplayed()
    }

    @Test
    fun contentDescriptionAndEditableSemantics() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("hello") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "cd_edit",
                    label = "Notes",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Notes field",
                        testTag = "notes_cd",
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("notes_cd")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Notes field")))
            .assert(hasSetTextAction())
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText))
    }

    @Test
    fun errorSemanticsOnPrimaryField() {
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
        composeRule.onNodeWithTag("sktextfield_email")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Error))
            .assert(hasSetTextAction())
    }

    @Test
    fun requiredFieldAccessibility() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "required_name",
                    label = "Name",
                    required = true,
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Name field",
                        testTag = "required_field",
                    ),
                )
            }
        }

        // Explicit CD preserved; required announced via stateDescription (not only visual '*').
        composeRule.onNodeWithTag("required_field")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Name field")))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Required"))
            .assert(hasSetTextAction())
        composeRule.onNodeWithText("Name *").assertIsDisplayed()
    }

    @Test
    fun disabledFieldAccessibility() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("keep-me") }
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

        composeRule.onNodeWithTag("disabled_field")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.SetText))

        assertEquals(
            0,
            composeRule.onAllNodes(hasSetTextAction(), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .size,
        )
        composeRule.onNodeWithText("keep-me").assertIsDisplayed()
    }

    @Test
    fun readOnlyFieldAccessibility() {
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

        composeRule.onNodeWithTag("readonly_field").assertIsDisplayed()
        composeRule.onNodeWithText("readonly@skone.io").assertIsDisplayed()
        // readOnly may still expose SetText; production must ignore edits.
        runCatching {
            composeRule.onNodeWithTag("readonly_field").performTextInput("changed")
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals("readonly@skone.io", valueState.value)
        }
        composeRule.onNodeWithText("changed").assertDoesNotExist()
    }

    @Test
    fun requiredRuleSubmitShowsErrorOnPrimaryNode() {
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
                            testTag = "name_required_submit",
                        ),
                    )
                }
            }
        }

        composeRule.runOnIdle {
            assertTrue(!form.submit().isValid)
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Required").assertIsDisplayed()
        composeRule.onNodeWithTag("name_required_submit")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Error))
    }

    @Test
    fun decorativeLeadingIconDoesNotExposeRawKeyAsContentDescription() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "icon_silent",
                    label = "Email",
                    leadingIcon = io.skone.component.framework.icon.SKIconKey("skone.icon.mail"),
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Email address",
                        testTag = "email_with_icon",
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("email_with_icon").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Email address").assertIsDisplayed()
        // Near-miss: raw key must NOT become a TalkBack sibling (pre-fix regression).
        composeRule
            .onAllNodes(hasContentDescription("skone.icon.mail"), useUnmergedTree = true)
            .assertCountEquals(0)
    }

    @Test
    fun explicitIconContentDescriptionIsAnnouncedSeparately() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "icon_loud",
                    label = "Search",
                    leadingIcon = io.skone.component.framework.icon.SKIconKey(
                        key = "skone.icon.search",
                        contentDescription = "Search glyph",
                    ),
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Search field",
                        testTag = "search_with_icon",
                    ),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Search field").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Search glyph").assertIsDisplayed()
        composeRule
            .onAllNodes(hasContentDescription("skone.icon.search"), useUnmergedTree = true)
            .assertCountEquals(0)
    }
}
