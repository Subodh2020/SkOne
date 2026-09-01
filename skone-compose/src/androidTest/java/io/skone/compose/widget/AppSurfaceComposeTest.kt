package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.SKTheme
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemeMode
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.navigation.SKNavigationItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Focused Compose tests for application-surface composition patterns
 * (list/filter, form submit gate, shell navigation selection).
 */
@RunWith(AndroidJUnit4::class)
class AppSurfaceComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun listPattern_searchEmptyAndFilterSheet() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var query by remember { mutableStateOf("") }
                var sheet by remember { mutableStateOf(false) }
                var unreadOnly by remember { mutableStateOf(false) }
                val rows = listOf("Ada", "Grace").filter {
                    query.isBlank() || it.contains(query, ignoreCase = true)
                }
                SKScaffold(
                    topBar = {
                        SKTopAppBar(
                            title = "Directory",
                            accessibility = SKAccessibilityConfig(testTag = "surf_list_bar"),
                        )
                    },
                    floatingActionButton = {
                        SKFab(
                            icon = SKIconKey("skone.icon.add", contentDescription = "Filters"),
                            onClick = { sheet = true },
                            accessibility = SKAccessibilityConfig(testTag = "surf_list_fab"),
                        )
                    },
                    contentSafeDrawing = false,
                ) {
                    SKSearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        accessibility = SKAccessibilityConfig(testTag = "surf_list_search"),
                    )
                    if (rows.isEmpty()) {
                        SKEmptyState(
                            title = "No people match",
                            primaryActionLabel = "Clear",
                            onPrimaryAction = { query = "" },
                            accessibility = SKAccessibilityConfig(testTag = "surf_list_empty"),
                        )
                    } else {
                        rows.forEach { SKText(text = it) }
                    }
                    if (unreadOnly) {
                        SKBadge(text = "Unread", accessibility = SKAccessibilityConfig(testTag = "surf_list_badge"))
                    }
                }
                SKBottomSheet(
                    visible = sheet,
                    onDismissRequest = { sheet = false },
                    title = "Filters",
                    primaryActionLabel = "Apply",
                    onPrimaryAction = {
                        unreadOnly = true
                        sheet = false
                    },
                    accessibility = SKAccessibilityConfig(testTag = "surf_list_sheet"),
                ) {
                    SKCheckbox(
                        checked = unreadOnly,
                        onCheckedChange = { unreadOnly = it },
                        label = "Unread only",
                        accessibility = SKAccessibilityConfig(testTag = "surf_list_unread"),
                    )
                }
            }
        }

        composeRule.onNodeWithTag("surf_list_search").assertIsDisplayed()
        composeRule.onNodeWithTag("surf_list_fab").performClick()
        composeRule.onNodeWithTag("surf_list_sheet").assertIsDisplayed()
        composeRule.onNodeWithText("Apply").performClick()
        composeRule.onNodeWithTag("surf_list_badge").assertIsDisplayed()
        composeRule.onNodeWithTag("surf_list_search").performTextInput("zzzz")
        composeRule.onNodeWithTag("surf_list_empty").assertIsDisplayed()
    }

    @Test
    fun formPattern_invalidSubmitDoesNotProceed() {
        var submitted = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                val form = remember { SKFormController.create() }
                var email by remember { mutableStateOf("") }
                var message by remember { mutableStateOf("") }
                ProvideSKFormController(form) {
                    SKTextField(
                        value = email,
                        onValueChange = { email = it },
                        fieldId = "email",
                        label = "Email",
                        required = true,
                        rules = listOf(SKRequiredRule(), SKEmailRule()),
                        accessibility = SKAccessibilityConfig(testTag = "surf_form_email"),
                    )
                    SKButton(
                        text = "Save",
                        onClick = {
                            if (form.validate().isValid) {
                                submitted++
                                message = "ok"
                            } else {
                                message = "invalid"
                            }
                        },
                        accessibility = SKAccessibilityConfig(testTag = "surf_form_submit"),
                    )
                    if (message.isNotEmpty()) {
                        SKText(text = message)
                    }
                }
            }
        }

        composeRule.onNodeWithTag("surf_form_submit").performClick()
        composeRule.onNodeWithText("invalid").assertIsDisplayed()
        assertEquals(0, submitted)
    }

    @Test
    fun shellPattern_navSelectionAndDisabledMenuItem() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var dest by remember { mutableStateOf("home") }
                var menuOpen by remember { mutableStateOf(false) }
                SKScaffold(
                    topBar = {
                        SKTopAppBar(
                            title = dest,
                            actionIcon = SKIconKey("skone.icon.more", contentDescription = "More"),
                            onActionClick = { menuOpen = true },
                            accessibility = SKAccessibilityConfig(testTag = "surf_shell_bar"),
                        )
                        SKDropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            items = listOf(
                                io.skone.ui.overlay.SKMenuItem("help", "Help"),
                                io.skone.ui.overlay.SKMenuItem("out", "Sign out", enabled = false),
                            ),
                            onItemClick = { menuOpen = false },
                            accessibility = SKAccessibilityConfig(testTag = "surf_shell_menu"),
                        )
                    },
                    bottomBar = {
                        SKNavigationBar(
                            items = listOf(
                                SKNavigationItem("home", "Home"),
                                SKNavigationItem("activity", "Activity"),
                                SKNavigationItem("settings", "Settings"),
                            ),
                            selectedId = dest,
                            onSelect = { dest = it },
                            accessibility = SKAccessibilityConfig(testTag = "surf_shell_nav"),
                        )
                    },
                    contentSafeDrawing = false,
                ) {
                    SKTabRow(
                        items = listOf(SKTabItem("feed", "Feed"), SKTabItem("following", "Following")),
                        selectedId = "feed",
                        onSelect = {},
                        accessibility = SKAccessibilityConfig(testTag = "surf_shell_tabs"),
                    )
                    SKText(text = "Destination $dest")
                }
            }
        }

        composeRule.onNodeWithTag("surf_shell_nav_activity").performClick()
        composeRule.onNodeWithText("Destination activity").assertIsDisplayed()
        composeRule.onNodeWithTag("surf_shell_bar_action").performClick()
        composeRule.onNodeWithTag("surf_shell_menu_out").assertIsNotEnabled()
        assertTrue(true)
    }
}
