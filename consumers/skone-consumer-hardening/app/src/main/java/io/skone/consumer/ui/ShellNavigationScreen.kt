package io.skone.consumer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKBadge
import io.skone.compose.widget.SKBottomAppBar
import io.skone.compose.widget.SKCard
import io.skone.compose.widget.SKDropdownMenu
import io.skone.compose.widget.SKFab
import io.skone.compose.widget.SKIconButton
import io.skone.compose.widget.SKListItem
import io.skone.compose.widget.SKNavigationBar
import io.skone.compose.widget.SKScaffold
import io.skone.compose.widget.SKSectionHeader
import io.skone.compose.widget.SKSnackbar
import io.skone.compose.widget.SKTabRow
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTooltip
import io.skone.compose.widget.SKTopAppBar
import io.skone.consumer.ShellDest
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.navigation.SKNavigationItem
import io.skone.ui.overlay.SKMenuItem

@Composable
fun ShellNavigationScreen(onBack: () -> Unit) {
    val theme = skTheme
    var destination by remember { mutableStateOf(ShellDest.Home) }
    var homeTab by remember { mutableStateOf("feed") }
    var menuExpanded by remember { mutableStateOf(false) }
    var snackVisible by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf("") }
    var tipVisible by remember { mutableStateOf(false) }

    SKScaffold(
        topBar = {
            Box {
                SKTopAppBar(
                    title = destination.label,
                    navigationIcon = SKIconKey("skone.icon.back", contentDescription = "Back"),
                    onNavigationClick = onBack,
                    actionIcon = SKIconKey("skone.icon.more", contentDescription = "More"),
                    onActionClick = { menuExpanded = true },
                    accessibility = SKAccessibilityConfig(testTag = "consumer_shell_topbar"),
                )
                SKDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    items = listOf(
                        SKMenuItem("refresh", "Refresh"),
                        SKMenuItem("help", "Help"),
                        SKMenuItem("out", "Sign out", enabled = false),
                    ),
                    onItemClick = {
                        menuExpanded = false
                        snackMessage = "Menu: $it"
                        snackVisible = true
                    },
                    accessibility = SKAccessibilityConfig(testTag = "consumer_shell_menu"),
                )
            }
        },
        bottomBar = {
            Column {
                if (destination == ShellDest.Home) {
                    SKBottomAppBar(
                        leading = {
                            SKIconButton(
                                icon = SKIconKey("skone.icon.search", contentDescription = "Search"),
                                onClick = {
                                    snackMessage = "Search"
                                    snackVisible = true
                                },
                            )
                        },
                        content = { SKText(text = "Quick actions") },
                        trailing = {
                            SKIconButton(
                                icon = SKIconKey("skone.icon.info", contentDescription = "Tip"),
                                onClick = { tipVisible = !tipVisible },
                            )
                        },
                    )
                    SKTooltip(
                        message = "Compose a new post",
                        visible = tipVisible,
                    )
                }
                SKNavigationBar(
                    items = ShellDest.entries.map {
                        SKNavigationItem(it.id, it.label, SKIconKey("skone.icon.nav", contentDescription = it.label))
                    },
                    selectedId = destination.id,
                    onSelect = { id -> destination = ShellDest.entries.first { it.id == id } },
                    accessibility = SKAccessibilityConfig(testTag = "consumer_shell_nav"),
                )
            }
        },
        snackbar = {
            SKSnackbar(
                message = snackMessage,
                visible = snackVisible,
                accessibility = SKAccessibilityConfig(testTag = "consumer_shell_snack"),
            )
        },
        floatingActionButton = {
            if (destination == ShellDest.Home) {
                SKFab(
                    icon = SKIconKey("skone.icon.add", contentDescription = "Compose"),
                    onClick = {
                        snackMessage = "Compose"
                        snackVisible = true
                    },
                )
            }
        },
        contentSafeDrawing = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(theme.tokens.spacing.sm.toDp()),
            verticalArrangement = Arrangement.spacedBy(theme.tokens.spacing.sm.toDp()),
        ) {
            when (destination) {
                ShellDest.Home -> {
                    SKTabRow(
                        items = listOf(SKTabItem("feed", "Feed"), SKTabItem("following", "Following")),
                        selectedId = homeTab,
                        onSelect = { homeTab = it },
                    )
                    SKCard(onClick = {
                        snackMessage = "Card"
                        snackVisible = true
                    }) {
                        SKText(text = "Welcome · $homeTab")
                    }
                    SKSectionHeader(title = "Highlights")
                    SKListItem(
                        headline = "Consumer hardening",
                        supportingText = "External Maven app",
                        trailingText = "New",
                        onClick = {},
                    )
                    SKBadge(text = "2")
                }
                ShellDest.Activity -> {
                    SKSectionHeader(title = "Activity")
                    listOf("Filter applied", "Profile saved").forEach {
                        SKListItem(headline = it, supportingText = "Just now", onClick = {})
                    }
                }
                ShellDest.Settings -> {
                    SKSectionHeader(title = "Settings")
                    SKListItem(headline = "Open list example", onClick = onBack)
                }
            }
        }
    }
}
