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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import io.skone.ui.navigation.SKNavigationItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKOverlayNavigationComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun snackbar_announcesMessage() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKSnackbar(
                    message = "Copied to clipboard",
                    accessibility = SKAccessibilityConfig(testTag = "sk_snack"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_snack")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion))
    }

    @Test
    fun alertDialog_confirm() {
        var confirmed = false
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var open by remember { mutableStateOf(true) }
                SKAlertDialog(
                    visible = open,
                    onConfirm = {
                        confirmed = true
                        open = false
                    },
                    onDismissRequest = { open = false },
                    title = "Delete file?",
                    message = "This cannot be undone.",
                    dismissLabel = null,
                    accessibility = SKAccessibilityConfig(testTag = "sk_alert"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_alert_confirm").performClick()
        composeRule.runOnIdle { assertTrue(confirmed) }
    }

    @Test
    fun topAppBar_withExplicitIconCd() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKTopAppBar(
                    title = "Home",
                    navigationIcon = SKIconKey("skone.icon.back", contentDescription = "Back"),
                    onNavigationClick = {},
                    accessibility = SKAccessibilityConfig(testTag = "sk_appbar"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_appbar").assertIsDisplayed()
        composeRule.onNodeWithTag("sk_appbar_nav").assertIsDisplayed()
    }

    @Test
    fun navigationBar_selectsExclusive() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var selected by remember { mutableStateOf("home") }
                SKNavigationBar(
                    items = listOf(
                        SKNavigationItem("home", "Home"),
                        SKNavigationItem("search", "Search"),
                    ),
                    selectedId = selected,
                    onSelect = { selected = it },
                    accessibility = SKAccessibilityConfig(testTag = "sk_nav"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_nav_home")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        composeRule.onNodeWithTag("sk_nav_search").performClick()
        composeRule.onNodeWithTag("sk_nav_search")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        composeRule.onNodeWithTag("sk_nav_home")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
    }

    @Test
    fun dialog_showsTitle() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKDialog(
                    visible = true,
                    onDismissRequest = {},
                    title = "Details",
                    accessibility = SKAccessibilityConfig(testTag = "sk_dialog"),
                ) {
                    SKText(text = "Body")
                }
            }
        }
        composeRule.onNodeWithTag("sk_dialog").assertIsDisplayed()
        composeRule.onNodeWithText("Details").assertIsDisplayed()
    }
}
