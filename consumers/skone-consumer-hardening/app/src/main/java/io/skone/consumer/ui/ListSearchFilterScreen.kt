package io.skone.consumer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKBottomSheet
import io.skone.compose.widget.SKButton
import io.skone.compose.widget.SKCheckbox
import io.skone.compose.widget.SKEmptyState
import io.skone.compose.widget.SKFab
import io.skone.compose.widget.SKListItem
import io.skone.compose.widget.SKProgressIndicator
import io.skone.compose.widget.SKScaffold
import io.skone.compose.widget.SKSearchBar
import io.skone.compose.widget.SKSectionHeader
import io.skone.compose.widget.SKSegmentedButton
import io.skone.compose.widget.SKSnackbar
import io.skone.compose.widget.SKTabRow
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTopAppBar
import io.skone.consumer.ConsumerLogic
import io.skone.consumer.Filters
import io.skone.consumer.ListPhase
import io.skone.consumer.PeopleTab
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.overlay.SKSegmentItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ListSearchFilterScreen(onBack: () -> Unit) {
    val theme = skTheme
    val scope = rememberCoroutineScope()
    var phase by remember { mutableStateOf(ListPhase.Loading) }
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(PeopleTab.All) }
    var draft by remember { mutableStateOf(Filters()) }
    var applied by remember { mutableStateOf(Filters()) }
    var sheetOpen by remember { mutableStateOf(false) }
    var snackVisible by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(400)
        phase = ListPhase.Ready
    }

    fun reload(error: Boolean) {
        phase = ListPhase.Loading
        scope.launch {
            delay(350)
            phase = if (error) ListPhase.Error else ListPhase.Ready
        }
    }

    val rows = remember(query, tab, applied, phase) {
        if (phase != ListPhase.Ready) emptyList()
        else ConsumerLogic.filter(query, tab, applied)
    }

    SKScaffold(
        topBar = {
            Column {
                SKTopAppBar(
                    title = "Directory",
                    navigationIcon = SKIconKey("skone.icon.back", contentDescription = "Back"),
                    onNavigationClick = onBack,
                    actionIcon = SKIconKey("skone.icon.filter", contentDescription = "Filters"),
                    onActionClick = { sheetOpen = true },
                    accessibility = SKAccessibilityConfig(testTag = "consumer_list_topbar"),
                )
                SKTabRow(
                    items = PeopleTab.entries.map { SKTabItem(it.id, it.label) },
                    selectedId = tab.id,
                    onSelect = { id -> tab = PeopleTab.entries.first { it.id == id } },
                    accessibility = SKAccessibilityConfig(testTag = "consumer_list_tabs"),
                )
            }
        },
        snackbar = {
            SKSnackbar(
                message = snackMessage,
                visible = snackVisible,
                accessibility = SKAccessibilityConfig(testTag = "consumer_list_snack"),
            )
        },
        floatingActionButton = {
            SKFab(
                icon = SKIconKey("skone.icon.add", contentDescription = "Open filters"),
                onClick = { sheetOpen = true },
                accessibility = SKAccessibilityConfig(testTag = "consumer_list_fab"),
            )
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
            SKSearchBar(
                query = query,
                onQueryChange = { query = it },
                onClear = { query = "" },
                placeholder = "Search people",
                accessibility = SKAccessibilityConfig(testTag = "consumer_list_search"),
            )
            SKSectionHeader(
                title = "People",
                supportingText = "${rows.size} visible",
                actionLabel = "Filters",
                onAction = { sheetOpen = true },
            )
            when (phase) {
                ListPhase.Loading -> {
                    SKProgressIndicator(
                        indeterminate = true,
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Loading",
                            testTag = "consumer_list_loading",
                        ),
                    )
                }
                ListPhase.Error -> {
                    SKEmptyState(
                        title = "Couldn’t load people",
                        description = "Try again.",
                        primaryActionLabel = "Retry",
                        onPrimaryAction = { reload(error = false) },
                        accessibility = SKAccessibilityConfig(testTag = "consumer_list_error"),
                    )
                }
                ListPhase.Ready -> {
                    if (rows.isEmpty()) {
                        SKEmptyState(
                            title = "No people match",
                            primaryActionLabel = "Clear search",
                            onPrimaryAction = { query = "" },
                            accessibility = SKAccessibilityConfig(testTag = "consumer_list_empty"),
                        )
                    } else {
                        rows.forEach { person ->
                            SKListItem(
                                headline = person.name,
                                supportingText = person.role,
                                selected = selectedId == person.id,
                                onClick = { selectedId = person.id },
                                accessibility = SKAccessibilityConfig(testTag = "consumer_list_row_${person.id}"),
                            )
                        }
                    }
                    SKButton(
                        text = "Simulate error",
                        onClick = { reload(error = true) },
                        accessibility = SKAccessibilityConfig(testTag = "consumer_list_force_error"),
                    )
                }
            }
        }
    }

    SKBottomSheet(
        visible = sheetOpen,
        onDismissRequest = {
            draft = applied
            sheetOpen = false
        },
        title = "Filters",
        primaryActionLabel = "Apply",
        onPrimaryAction = {
            applied = draft
            sheetOpen = false
            snackMessage = "Filters applied"
            snackVisible = true
        },
        secondaryActionLabel = "Reset",
        onSecondaryAction = {
            draft = Filters()
            applied = Filters()
            sheetOpen = false
        },
        accessibility = SKAccessibilityConfig(testTag = "consumer_list_sheet"),
    ) {
        SKSegmentedButton(
            items = listOf(
                SKSegmentItem("all", "All"),
                SKSegmentItem("team", "Team"),
                SKSegmentItem("starred", "Starred", enabled = false),
            ),
            selectedId = tab.id,
            onSelect = { id ->
                if (id != "starred") tab = PeopleTab.entries.first { it.id == id }
            },
        )
        SKCheckbox(
            checked = draft.unreadOnly,
            onCheckedChange = { draft = draft.copy(unreadOnly = it) },
            label = "Unread only",
            accessibility = SKAccessibilityConfig(testTag = "consumer_list_unread"),
        )
        SKCheckbox(
            checked = false,
            onCheckedChange = {},
            enabled = false,
            label = "Include archived (unavailable)",
            accessibility = SKAccessibilityConfig(testTag = "consumer_list_archived"),
        )
        SKText(text = "Starred segment stays disabled on purpose.")
    }
}
