package io.skone.playground.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKAlertDialog
import io.skone.compose.widget.SKAvatar
import io.skone.compose.widget.SKBadge
import io.skone.compose.widget.SKBottomAppBar
import io.skone.compose.widget.SKBottomSheet
import io.skone.compose.widget.SKButton
import io.skone.compose.widget.SKCard
import io.skone.compose.widget.SKCheckbox
import io.skone.compose.widget.SKDropdownMenu
import io.skone.compose.widget.SKEmptyState
import io.skone.compose.widget.SKFab
import io.skone.compose.widget.SKIconButton
import io.skone.compose.widget.SKListItem
import io.skone.compose.widget.SKNavigationBar
import io.skone.compose.widget.SKProgressIndicator
import io.skone.compose.widget.SKScaffold
import io.skone.compose.widget.SKSearchBar
import io.skone.compose.widget.SKSectionHeader
import io.skone.compose.widget.SKSegmentedButton
import io.skone.compose.widget.SKSnackbar
import io.skone.compose.widget.SKSwitch
import io.skone.compose.widget.SKTabRow
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.compose.widget.SKTooltip
import io.skone.compose.widget.SKTopAppBar
import io.skone.forms.SKFormController
import io.skone.forms.formatter.SKTrimFormatter
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKMinLengthRule
import io.skone.forms.validation.SKRequiredRule
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.navigation.SKNavigationItem
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import io.skone.ui.overlay.SKMenuItem
import io.skone.ui.overlay.SKSegmentItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Surface A — List + Search + Filter.
 *
 * Host owns load phase, query, tab, filters, sheet, snackbar.
 */
@Composable
fun AppListSearchFilterSurface() {
    val theme = skTheme
    val scope = rememberCoroutineScope()
    var phase by remember { mutableStateOf(ListLoadPhase.Loading) }
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(PeopleTab.All) }
    var draftFilters by remember { mutableStateOf(DirectoryFilters()) }
    var appliedFilters by remember { mutableStateOf(DirectoryFilters()) }
    var sheetOpen by remember { mutableStateOf(false) }
    var snackVisible by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(450)
        phase = ListLoadPhase.Ready
    }

    fun reload(forceError: Boolean = false) {
        phase = ListLoadPhase.Loading
        scope.launch {
            delay(400)
            phase = if (forceError) ListLoadPhase.Error else ListLoadPhase.Ready
        }
    }

    val filtered = remember(query, tab, appliedFilters, phase) {
        if (phase != ListLoadPhase.Ready) emptyList()
        else AppSurfaceLogic.filterDirectory(
            people = AppSurfaceLogic.sampleDirectory,
            query = query,
            tab = tab,
            filters = appliedFilters,
        )
    }

    SKScaffold(
        topBar = {
            Column {
                SKTopAppBar(
                    title = "Directory",
                    actionIcon = SKIconKey("skone.icon.filter", contentDescription = "Open filters"),
                    onActionClick = { sheetOpen = true },
                    accessibility = SKAccessibilityConfig(testTag = "app_list_topbar"),
                )
                SKTabRow(
                    items = PeopleTab.entries.map { SKTabItem(it.id, it.label) },
                    selectedId = tab.id,
                    onSelect = { id ->
                        tab = PeopleTab.entries.first { it.id == id }
                    },
                    accessibility = SKAccessibilityConfig(testTag = "app_list_tabs"),
                )
            }
        },
        snackbar = {
            SKSnackbar(
                message = snackMessage,
                actionLabel = "Undo",
                onAction = {
                    appliedFilters = DirectoryFilters()
                    draftFilters = DirectoryFilters()
                    snackVisible = false
                },
                visible = snackVisible,
                accessibility = SKAccessibilityConfig(testTag = "app_list_snack"),
            )
        },
        floatingActionButton = {
            SKFab(
                icon = SKIconKey("skone.icon.add", contentDescription = "Invite person"),
                onClick = { sheetOpen = true },
                accessibility = SKAccessibilityConfig(testTag = "app_list_fab"),
            )
        },
        contentSafeDrawing = false,
        accessibility = SKAccessibilityConfig(testTag = "app_list_scaffold"),
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
                placeholder = "Search people",
                onClear = { query = "" },
                accessibility = SKAccessibilityConfig(testTag = "app_list_search"),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(theme.tokens.spacing.sm.toDp()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SKAvatar(
                    initials = "SK",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Workspace",
                        testTag = "app_list_avatar",
                    ),
                )
                Column(modifier = Modifier.weight(1f)) {
                    SKText(text = "Workspace members")
                    SKText(text = "${filtered.size} visible · tab ${tab.label}")
                }
                if (appliedFilters.unreadOnly) {
                    SKBadge(
                        text = "Unread",
                        accessibility = SKAccessibilityConfig(testTag = "app_list_badge"),
                    )
                }
            }
            SKSectionHeader(
                title = "People",
                supportingText = when (phase) {
                    ListLoadPhase.Loading -> "Loading…"
                    ListLoadPhase.Error -> "Something went wrong"
                    ListLoadPhase.Ready -> if (filtered.isEmpty()) "No matches" else "Tap a row"
                },
                actionLabel = "Filters",
                onAction = { sheetOpen = true },
                accessibility = SKAccessibilityConfig(testTag = "app_list_header"),
            )

            when (phase) {
                ListLoadPhase.Loading -> {
                    SKProgressIndicator(
                        indeterminate = true,
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Loading directory",
                            testTag = "app_list_loading",
                        ),
                    )
                    SKText(text = "Fetching directory…")
                }
                ListLoadPhase.Error -> {
                    SKEmptyState(
                        title = "Couldn’t load people",
                        description = "Check your connection and try again.",
                        primaryActionLabel = "Retry",
                        onPrimaryAction = { reload(forceError = false) },
                        secondaryActionLabel = "Simulate error",
                        onSecondaryAction = { reload(forceError = true) },
                        accessibility = SKAccessibilityConfig(testTag = "app_list_error"),
                    )
                }
                ListLoadPhase.Ready -> {
                    if (filtered.isEmpty()) {
                        SKEmptyState(
                            title = "No people match",
                            description = "Clear search or adjust filters.",
                            primaryActionLabel = "Clear search",
                            onPrimaryAction = { query = "" },
                            secondaryActionLabel = "Reset filters",
                            onSecondaryAction = {
                                appliedFilters = DirectoryFilters()
                                draftFilters = DirectoryFilters()
                            },
                            accessibility = SKAccessibilityConfig(testTag = "app_list_empty"),
                        )
                    } else {
                        filtered.forEach { person ->
                            SKListItem(
                                headline = person.name,
                                supportingText = person.role,
                                leadingIcon = SKIconKey("skone.icon.person"),
                                selected = selectedId == person.id,
                                onClick = { selectedId = person.id },
                                accessibility = SKAccessibilityConfig(testTag = "app_list_row_${person.id}"),
                            )
                        }
                    }
                    SKButton(
                        text = "Simulate load error",
                        onClick = { reload(forceError = true) },
                        accessibility = SKAccessibilityConfig(testTag = "app_list_force_error"),
                    )
                }
            }
        }
    }

    SKBottomSheet(
        visible = sheetOpen,
        onDismissRequest = {
            draftFilters = appliedFilters
            sheetOpen = false
        },
        title = "Filters",
        primaryActionLabel = "Apply",
        onPrimaryAction = {
            appliedFilters = draftFilters
            sheetOpen = false
            snackMessage = if (draftFilters.unreadOnly) "Unread filter applied" else "Filters updated"
            snackVisible = true
        },
        secondaryActionLabel = "Reset",
        onSecondaryAction = {
            draftFilters = DirectoryFilters()
            appliedFilters = DirectoryFilters()
            sheetOpen = false
            snackMessage = "Filters cleared"
            snackVisible = true
        },
        accessibility = SKAccessibilityConfig(testTag = "app_list_sheet"),
    ) {
        SKSegmentedButton(
            items = listOf(
                SKSegmentItem("all", "All"),
                SKSegmentItem("team", "Team"),
                SKSegmentItem("starred", "Starred", enabled = false),
            ),
            selectedId = when (tab) {
                PeopleTab.All -> "all"
                PeopleTab.Team -> "team"
                PeopleTab.Starred -> "starred"
            },
            onSelect = { id ->
                when (id) {
                    "team" -> tab = PeopleTab.Team
                    "starred" -> { /* disabled */ }
                    else -> tab = PeopleTab.All
                }
            },
            accessibility = SKAccessibilityConfig(testTag = "app_list_sheet_seg"),
        )
        SKText(text = "Starred segment stays disabled to validate disabled selection.")
        SKCheckbox(
            checked = draftFilters.unreadOnly,
            onCheckedChange = { draftFilters = draftFilters.copy(unreadOnly = it) },
            label = "Unread only",
            accessibility = SKAccessibilityConfig(testTag = "app_list_unread"),
        )
        SKCheckbox(
            checked = draftFilters.includeArchived,
            onCheckedChange = { },
            enabled = false,
            label = "Include archived (unavailable)",
            accessibility = SKAccessibilityConfig(testTag = "app_list_archived"),
        )
        SKButton(
            text = "Close",
            onClick = {
                draftFilters = appliedFilters
                sheetOpen = false
            },
            accessibility = SKAccessibilityConfig(testTag = "app_list_sheet_close"),
        )
    }
}

/**
 * Surface B — Form + Validation.
 *
 * Uses [SKFormController] for field validation; host owns submit loading / success / failure.
 */
@Composable
fun AppFormValidationSurface() {
    val theme = skTheme
    val scope = rememberCoroutineScope()
    val form = remember { SKFormController.create() }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("Engineer") }
    var notifications by remember { mutableStateOf(true) }
    var marketing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<FormUiStatus>(FormUiStatus.Idle) }
    var snackVisible by remember { mutableStateOf(false) }
    var confirmDiscard by remember { mutableStateOf(false) }
    var forceFail by remember { mutableStateOf(false) }

    val submitting = status is FormUiStatus.Submitting
    val snapshot = FormFieldSnapshot(displayName, email, jobTitle, notifications, marketing)

    SKScaffold(
        topBar = {
            SKTopAppBar(
                title = "Profile settings",
                accessibility = SKAccessibilityConfig(testTag = "app_form_topbar"),
            )
        },
        snackbar = {
            SKSnackbar(
                message = when (val s = status) {
                    is FormUiStatus.Success -> "Profile saved"
                    is FormUiStatus.Failure -> s.message
                    else -> ""
                },
                visible = snackVisible,
                accessibility = SKAccessibilityConfig(testTag = "app_form_snack"),
            )
        },
        contentSafeDrawing = false,
        accessibility = SKAccessibilityConfig(testTag = "app_form_scaffold"),
    ) {
        ProvideSKFormController(form) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(theme.tokens.spacing.md.toDp()),
                verticalArrangement = Arrangement.spacedBy(theme.tokens.spacing.md.toDp()),
            ) {
                SKSectionHeader(
                    title = "Account",
                    supportingText = "Validate before save. Loading blocks duplicate submit.",
                    accessibility = SKAccessibilityConfig(testTag = "app_form_header"),
                )
                SKTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = displayName,
                    onValueChange = {
                        displayName = it
                        if (status !is FormUiStatus.Submitting) status = FormUiStatus.Idle
                    },
                    fieldId = "displayName",
                    label = "Display name",
                    hint = "At least 2 characters",
                    required = true,
                    enabled = !submitting,
                    rules = listOf(SKRequiredRule(), SKMinLengthRule(2)),
                    formatter = SKTrimFormatter,
                    imeAction = SKImeAction.Next,
                    accessibility = SKAccessibilityConfig(testTag = "app_form_name"),
                )
                SKTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = {
                        email = it
                        if (status !is FormUiStatus.Submitting) status = FormUiStatus.Idle
                    },
                    fieldId = "email",
                    label = "Email",
                    hint = "name@company.com",
                    required = true,
                    enabled = !submitting,
                    rules = listOf(SKRequiredRule(), SKEmailRule()),
                    formatter = SKTrimFormatter,
                    keyboardType = SKKeyboardType.Email,
                    imeAction = SKImeAction.Next,
                    accessibility = SKAccessibilityConfig(testTag = "app_form_email"),
                )
                SKTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
                    fieldId = "title",
                    label = "Job title",
                    enabled = false,
                    hint = "Managed by admin",
                    accessibility = SKAccessibilityConfig(testTag = "app_form_title"),
                )
                SKSwitch(
                    checked = notifications,
                    onCheckedChange = { notifications = it },
                    enabled = !submitting,
                    label = "Email notifications",
                    accessibility = SKAccessibilityConfig(testTag = "app_form_notifications"),
                )
                SKCheckbox(
                    checked = marketing,
                    onCheckedChange = { marketing = it },
                    enabled = !submitting,
                    label = "Product updates",
                    accessibility = SKAccessibilityConfig(testTag = "app_form_marketing"),
                )
                if (submitting) {
                    SKProgressIndicator(
                        indeterminate = true,
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Saving profile",
                            testTag = "app_form_loading",
                        ),
                    )
                }
                SKCheckbox(
                    checked = forceFail,
                    onCheckedChange = { forceFail = it },
                    enabled = !submitting,
                    label = "Simulate server failure",
                    accessibility = SKAccessibilityConfig(testTag = "app_form_force_fail"),
                )
                SKButton(
                    text = if (submitting) "Saving…" else "Save profile",
                    enabled = !submitting,
                    loading = submitting,
                    onClick = {
                        val validation = form.validate()
                        if (!validation.isValid || !AppSurfaceLogic.canSubmitProfile(snapshot)) {
                            status = FormUiStatus.Failure("Fix validation errors before saving")
                            snackVisible = true
                        } else {
                            status = FormUiStatus.Submitting
                            snackVisible = false
                            scope.launch {
                                delay(700)
                                if (forceFail) {
                                    status = FormUiStatus.Failure("Server rejected the update")
                                    snackVisible = true
                                } else {
                                    form.submit()
                                    status = FormUiStatus.Success
                                    snackVisible = true
                                }
                            }
                        }
                    },
                    accessibility = SKAccessibilityConfig(testTag = "app_form_submit"),
                )
                SKButton(
                    text = "Discard changes",
                    enabled = !submitting,
                    onClick = { confirmDiscard = true },
                    accessibility = SKAccessibilityConfig(testTag = "app_form_discard"),
                )
            }
        }
    }

    SKAlertDialog(
        visible = confirmDiscard,
        title = "Discard changes?",
        message = "Unsaved profile edits will be lost.",
        confirmLabel = "Discard",
        dismissLabel = "Keep editing",
        onConfirm = {
            displayName = ""
            email = ""
            marketing = false
            notifications = true
            status = FormUiStatus.Idle
            snackVisible = false
            form.reset()
            confirmDiscard = false
        },
        onDismissRequest = { confirmDiscard = false },
        accessibility = SKAccessibilityConfig(testTag = "app_form_discard_dialog"),
    )
}

/**
 * Surface C — App shell + lightweight local navigation (3 destinations).
 */
@Composable
fun AppShellNavigationSurface() {
    val theme = skTheme
    var destination by remember { mutableStateOf(ShellDestination.Home) }
    var homeTab by remember { mutableStateOf("feed") }
    var menuExpanded by remember { mutableStateOf(false) }
    var snackVisible by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf("") }
    var tooltipVisible by remember { mutableStateOf(false) }

    val navItems = remember {
        ShellDestination.entries.map {
            SKNavigationItem(
                id = it.id,
                label = it.label,
                icon = SKIconKey("skone.icon.nav", contentDescription = it.label),
            )
        }
    }

    SKScaffold(
        topBar = {
            Box {
                SKTopAppBar(
                    title = AppSurfaceLogic.shellTitle(destination),
                    actionIcon = SKIconKey("skone.icon.more", contentDescription = "More actions"),
                    onActionClick = { menuExpanded = true },
                    accessibility = SKAccessibilityConfig(testTag = "app_shell_topbar"),
                )
                SKDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    items = listOf(
                        SKMenuItem("refresh", "Refresh"),
                        SKMenuItem("help", "Help"),
                        SKMenuItem("signout", "Sign out", enabled = false),
                    ),
                    onItemClick = { id ->
                        menuExpanded = false
                        snackMessage = "Menu: $id"
                        snackVisible = true
                    },
                    accessibility = SKAccessibilityConfig(testTag = "app_shell_menu"),
                )
            }
        },
        bottomBar = {
            Column {
                if (destination == ShellDestination.Home) {
                    SKBottomAppBar(
                        leading = {
                            SKIconButton(
                                icon = SKIconKey("skone.icon.search", contentDescription = "Search"),
                                onClick = {
                                    snackMessage = "Search from bottom bar"
                                    snackVisible = true
                                },
                                accessibility = SKAccessibilityConfig(testTag = "app_shell_bab_search"),
                            )
                        },
                        content = {
                            SKText(text = "Quick actions")
                        },
                        trailing = {
                            SKIconButton(
                                icon = SKIconKey("skone.icon.info", contentDescription = "Show tip"),
                                onClick = { tooltipVisible = !tooltipVisible },
                                accessibility = SKAccessibilityConfig(testTag = "app_shell_tip"),
                            )
                        },
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_bab"),
                    )
                    SKTooltip(
                        message = "Compose a new post",
                        visible = tooltipVisible,
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_tooltip"),
                    )
                }
                SKNavigationBar(
                    items = navItems,
                    selectedId = destination.id,
                    onSelect = { id ->
                        destination = ShellDestination.entries.first { it.id == id }
                    },
                    accessibility = SKAccessibilityConfig(testTag = "app_shell_nav"),
                )
            }
        },
        snackbar = {
            SKSnackbar(
                message = snackMessage,
                visible = snackVisible,
                accessibility = SKAccessibilityConfig(testTag = "app_shell_snack"),
            )
        },
        floatingActionButton = {
            if (destination == ShellDestination.Home) {
                SKFab(
                    icon = SKIconKey("skone.icon.add", contentDescription = "Compose"),
                    onClick = {
                        snackMessage = "Compose opened"
                        snackVisible = true
                    },
                    accessibility = SKAccessibilityConfig(testTag = "app_shell_fab"),
                )
            }
        },
        contentSafeDrawing = false,
        accessibility = SKAccessibilityConfig(testTag = "app_shell_scaffold"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(theme.tokens.spacing.sm.toDp()),
            verticalArrangement = Arrangement.spacedBy(theme.tokens.spacing.sm.toDp()),
        ) {
            when (destination) {
                ShellDestination.Home -> {
                    SKTabRow(
                        items = listOf(
                            SKTabItem("feed", "Feed"),
                            SKTabItem("following", "Following"),
                        ),
                        selectedId = homeTab,
                        onSelect = { homeTab = it },
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_home_tabs"),
                    )
                    SKCard(
                        onClick = {
                            snackMessage = "Opened card"
                            snackVisible = true
                        },
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_card"),
                    ) {
                        SKText(text = "Welcome card · $homeTab")
                        SKText(text = "SKCard + tabs compose without a navigation framework.")
                    }
                    SKSectionHeader(title = "Highlights", supportingText = "Local destination state")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(theme.tokens.spacing.sm.toDp()),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SKListItem(
                            modifier = Modifier.weight(1f),
                            headline = "Ship application surfaces",
                            supportingText = "Integration milestone",
                            leadingIcon = SKIconKey("skone.icon.star"),
                            trailingText = "New",
                            onClick = {
                                snackMessage = "Opened highlight"
                                snackVisible = true
                            },
                            accessibility = SKAccessibilityConfig(testTag = "app_shell_list"),
                        )
                        SKBadge(
                            text = "2",
                            accessibility = SKAccessibilityConfig(testTag = "app_shell_badge"),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(theme.tokens.spacing.sm.toDp())) {
                        SKAvatar(
                            initials = "YO",
                            accessibility = SKAccessibilityConfig(
                                contentDescription = "You",
                                testTag = "app_shell_avatar",
                            ),
                        )
                        SKText(text = "Signed in as You")
                    }
                }
                ShellDestination.Activity -> {
                    SKSectionHeader(title = "Activity", supportingText = "Recent events")
                    listOf(
                        "Filter applied on Directory",
                        "Profile draft saved",
                        "Opened Help from menu",
                    ).forEachIndexed { index, line ->
                        SKListItem(
                            headline = line,
                            supportingText = "Just now",
                            onClick = {
                                snackMessage = line
                                snackVisible = true
                            },
                            accessibility = SKAccessibilityConfig(testTag = "app_shell_activity_$index"),
                        )
                    }
                }
                ShellDestination.Settings -> {
                    SKSectionHeader(
                        title = "Settings",
                        supportingText = "Shell destination",
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_settings_header"),
                    )
                    SKListItem(
                        headline = "Open Directory example",
                        supportingText = "Use Samples → List + Search + Filter",
                        onClick = {
                            snackMessage = "Navigate via Samples tab"
                            snackVisible = true
                        },
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_settings_directory"),
                    )
                    SKListItem(
                        headline = "Open Form example",
                        supportingText = "Use Samples → Form + Validation",
                        onClick = {
                            snackMessage = "Navigate via Samples tab"
                            snackVisible = true
                        },
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_settings_form"),
                    )
                    SKSwitch(
                        checked = true,
                        onCheckedChange = {
                            snackMessage = "Preference updated"
                            snackVisible = true
                        },
                        label = "Compact density (demo)",
                        accessibility = SKAccessibilityConfig(testTag = "app_shell_settings_switch"),
                    )
                }
            }
        }
    }
}
