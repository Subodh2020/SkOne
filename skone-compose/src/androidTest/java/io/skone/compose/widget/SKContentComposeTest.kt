package io.skone.compose.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKContentComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun listItem_clickSelectedDisabled() {
        var clicks = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var selected by remember { mutableStateOf(false) }
                SKListItem(
                    headline = "Notifications",
                    supportingText = "Push and email",
                    trailingText = "On",
                    leadingIcon = SKIconKey("skone.icon.bell"),
                    selected = selected,
                    enabled = true,
                    onClick = {
                        clicks++
                        selected = true
                    },
                    accessibility = SKAccessibilityConfig(testTag = "sk_list_item"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_list_item")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
            .performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
        composeRule.onNodeWithTag("sk_list_item")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
    }

    @Test
    fun listItem_disabledBlocksShortcutPath() {
        var clicks = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKListItem(
                    headline = "Locked",
                    enabled = false,
                    onClick = { clicks++ },
                    accessibility = SKAccessibilityConfig(testTag = "sk_list_disabled"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_list_disabled")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
            .performClick()
        composeRule.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun listItem_longTextStillDisplays() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKListItem(
                    headline = "H".repeat(120),
                    supportingText = "S".repeat(200),
                    accessibility = SKAccessibilityConfig(testTag = "sk_list_long"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_list_long").assertIsDisplayed()
    }

    @Test
    fun sectionHeader_headingAndAction() {
        var actions = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKSectionHeader(
                    title = "Favorites",
                    supportingText = "Pinned items",
                    actionLabel = "Edit",
                    onAction = { actions++ },
                    accessibility = SKAccessibilityConfig(testTag = "sk_section"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_section")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        composeRule.onNodeWithText("Edit").performClick()
        composeRule.runOnIdle { assertEquals(1, actions) }
    }

    @Test
    fun scaffold_realisticScreenComposition() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var selected by remember { mutableStateOf("home") }
                var snackVisible by remember { mutableStateOf(false) }
                SKScaffold(
                    topBar = {
                        SKTopAppBar(
                            title = "Inbox",
                            navigationIcon = SKIconKey("skone.icon.menu", contentDescription = "Menu"),
                            onNavigationClick = {},
                        )
                    },
                    bottomBar = {
                        SKNavigationBar(
                            items = listOf(
                                SKNavigationItem("home", "Home"),
                                SKNavigationItem("search", "Search"),
                            ),
                            selectedId = selected,
                            onSelect = { selected = it },
                        )
                    },
                    snackbar = {
                        if (snackVisible) {
                            SKSnackbar(message = "Synced", visible = true)
                        }
                    },
                    accessibility = SKAccessibilityConfig(testTag = "sk_scaffold"),
                    contentSafeDrawing = false,
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        SKSectionHeader(title = "Today", actionLabel = "See all", onAction = {})
                        SKListItem(
                            headline = "Welcome",
                            supportingText = "Tap to open",
                            onClick = { snackVisible = true },
                            accessibility = SKAccessibilityConfig(testTag = "sk_scaffold_row"),
                        )
                        SKListItem(headline = "Empty optional fields only")
                    }
                }
            }
        }
        composeRule.onNodeWithTag("sk_scaffold").assertIsDisplayed()
        composeRule.onNodeWithText("Inbox").assertIsDisplayed()
        composeRule.onNodeWithTag("sk_scaffold_row").performClick()
        composeRule.runOnIdle { assertTrue(true) }
        composeRule.onNodeWithText("Synced").assertIsDisplayed()
    }

    @Test
    fun scaffold_contentOnlyNoBars() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKScaffold(
                    accessibility = SKAccessibilityConfig(testTag = "sk_scaffold_bare"),
                    contentSafeDrawing = false,
                ) {
                    SKText(text = "Bare content")
                }
            }
        }
        composeRule.onNodeWithTag("sk_scaffold_bare").assertIsDisplayed()
        composeRule.onNodeWithText("Bare content").assertIsDisplayed()
    }
}
