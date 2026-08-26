package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.SKTheme
import io.skone.forms.SKFormController
import io.skone.theme.SKThemeMode
import io.skone.ui.field.SKImeAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented contracts: IME Next must move actual Compose focus via the form focus chain.
 */
@RunWith(AndroidJUnit4::class)
class SKTextFieldImeFocusComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun editable(tag: String) =
        composeRule.onNode(
            hasSetTextAction() and hasAnyAncestor(hasTestTag(tag)),
            useUnmergedTree = true,
        )

    @Test
    fun firstFieldImeNextMovesFocusToSecondField() {
        lateinit var form: SKFormController
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var a by remember { mutableStateOf("") }
                    var b by remember { mutableStateOf("") }
                    SKTextField(
                        value = a,
                        onValueChange = { a = it },
                        fieldId = "a",
                        label = "A",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_a"),
                    )
                    SKTextField(
                        value = b,
                        onValueChange = { b = it },
                        fieldId = "b",
                        label = "B",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_b"),
                    )
                }
            }
        }

        editable("field_a").requestFocus()
        composeRule.waitForIdle()
        editable("field_a").assertIsFocused()

        editable("field_a").performImeAction()
        composeRule.waitForIdle()

        editable("field_b").assertIsFocused()
        editable("field_a").assertIsNotFocused()
        assertEquals("b", form.focus.focusedId.value)
    }

    @Test
    fun secondFieldImeNextMovesFocusToThirdField() {
        lateinit var form: SKFormController
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var a by remember { mutableStateOf("") }
                    var b by remember { mutableStateOf("") }
                    var c by remember { mutableStateOf("") }
                    SKTextField(
                        value = a,
                        onValueChange = { a = it },
                        fieldId = "a",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_a"),
                    )
                    SKTextField(
                        value = b,
                        onValueChange = { b = it },
                        fieldId = "b",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_b"),
                    )
                    SKTextField(
                        value = c,
                        onValueChange = { c = it },
                        fieldId = "c",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_c"),
                    )
                }
            }
        }

        editable("field_b").requestFocus()
        composeRule.waitForIdle()

        editable("field_b").performImeAction()
        composeRule.waitForIdle()

        editable("field_c").assertIsFocused()
        assertEquals("c", form.focus.focusedId.value)
    }

    @Test
    fun lastFieldImeNextDoesNotMoveFocus() {
        lateinit var form: SKFormController
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var a by remember { mutableStateOf("") }
                    var b by remember { mutableStateOf("") }
                    SKTextField(
                        value = a,
                        onValueChange = { a = it },
                        fieldId = "a",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_a"),
                    )
                    SKTextField(
                        value = b,
                        onValueChange = { b = it },
                        fieldId = "b",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_b"),
                    )
                }
            }
        }

        editable("field_b").requestFocus()
        composeRule.waitForIdle()

        editable("field_b").performImeAction()
        composeRule.waitForIdle()

        // Chain clears focusedId; Compose focus stays on B (no invalid target).
        assertNull(form.focus.focusedId.value)
        editable("field_b").assertIsFocused()
        editable("field_a").assertIsNotFocused()
    }

    @Test
    fun imeNextWithoutFormDoesNotCrash() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableStateOf("") }
                SKTextField(
                    value = value,
                    onValueChange = { value = it },
                    fieldId = "solo",
                    imeAction = SKImeAction.Next,
                    accessibility = SKAccessibilityConfig(testTag = "solo"),
                )
            }
        }

        editable("solo").requestFocus()
        composeRule.waitForIdle()
        editable("solo").performImeAction()
        composeRule.waitForIdle()

        editable("solo").assertIsFocused()
    }

    @Test
    fun fieldLeavingCompositionDoesNotLeaveStaleFocusRequester() {
        lateinit var form: SKFormController
        var showMiddle by mutableStateOf(true)
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var a by remember { mutableStateOf("") }
                    var b by remember { mutableStateOf("") }
                    var c by remember { mutableStateOf("") }
                    SKTextField(
                        value = a,
                        onValueChange = { a = it },
                        fieldId = "a",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_a"),
                    )
                    if (showMiddle) {
                        SKTextField(
                            value = b,
                            onValueChange = { b = it },
                            fieldId = "b",
                            imeAction = SKImeAction.Next,
                            accessibility = SKAccessibilityConfig(testTag = "field_b"),
                        )
                    }
                    SKTextField(
                        value = c,
                        onValueChange = { c = it },
                        fieldId = "c",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_c"),
                    )
                }
            }
        }

        editable("field_a").requestFocus()
        composeRule.waitForIdle()

        composeRule.runOnIdle { showMiddle = false }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("field_b").assertDoesNotExist()

        editable("field_a").performImeAction()
        composeRule.waitForIdle()

        editable("field_c").assertIsFocused()
        assertEquals("c", form.focus.focusedId.value)
        assertEquals(listOf("a", "c"), form.focus.order)
    }

    /**
     * Documents current focus-chain contract: disabled fields are NOT skipped.
     * Next from A still targets B in the chain; Compose may be unable to focus a disabled field.
     */
    @Test
    fun imeNextTargetsDisabledNextFieldPerFocusChainContract() {
        lateinit var form: SKFormController
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                form = remember { SKFormController.create() }
                ProvideSKFormController(form) {
                    var a by remember { mutableStateOf("") }
                    var b by remember { mutableStateOf("") }
                    SKTextField(
                        value = a,
                        onValueChange = { a = it },
                        fieldId = "a",
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_a"),
                    )
                    SKTextField(
                        value = b,
                        onValueChange = { b = it },
                        fieldId = "b",
                        enabled = false,
                        imeAction = SKImeAction.Next,
                        accessibility = SKAccessibilityConfig(testTag = "field_b"),
                    )
                }
            }
        }

        editable("field_a").requestFocus()
        composeRule.waitForIdle()

        editable("field_a").performImeAction()
        composeRule.waitForIdle()

        // Form chain advances to "b" — SKDefaultFocusChain does not skip disabled/readOnly.
        // Compose focus onto a disabled BasicTextField via FocusRequester is platform-dependent;
        // no skip-to-next-enabled rule is defined, so this test only locks the chain contract.
        assertEquals("b", form.focus.focusedId.value)
        composeRule.onNodeWithTag("field_b").assertExists()
    }
}
