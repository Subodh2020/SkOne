package io.skone.consumer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKAlertDialog
import io.skone.compose.widget.SKButton
import io.skone.compose.widget.SKCheckbox
import io.skone.compose.widget.SKProgressIndicator
import io.skone.compose.widget.SKScaffold
import io.skone.compose.widget.SKSectionHeader
import io.skone.compose.widget.SKSnackbar
import io.skone.compose.widget.SKSwitch
import io.skone.compose.widget.SKTextField
import io.skone.compose.widget.SKTopAppBar
import io.skone.consumer.ConsumerLogic
import io.skone.consumer.FormStatus
import io.skone.consumer.ProfileDraft
import io.skone.forms.SKFormController
import io.skone.forms.formatter.SKTrimFormatter
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKMinLengthRule
import io.skone.forms.validation.SKRequiredRule
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FormValidationScreen(onBack: () -> Unit) {
    val theme = skTheme
    val scope = rememberCoroutineScope()
    val form = remember { SKFormController.create() }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Engineer") }
    var notifications by remember { mutableStateOf(true) }
    var marketing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<FormStatus>(FormStatus.Idle) }
    var snackVisible by remember { mutableStateOf(false) }
    var confirmDiscard by remember { mutableStateOf(false) }
    var forceFail by remember { mutableStateOf(false) }
    val submitting = status is FormStatus.Submitting
    val draft = ProfileDraft(displayName, email)

    SKScaffold(
        topBar = {
            SKTopAppBar(
                title = "Profile settings",
                navigationIcon = SKIconKey("skone.icon.back", contentDescription = "Back"),
                onNavigationClick = onBack,
                accessibility = SKAccessibilityConfig(testTag = "consumer_form_topbar"),
            )
        },
        snackbar = {
            SKSnackbar(
                message = when (val s = status) {
                    is FormStatus.Success -> "Profile saved"
                    is FormStatus.Failure -> s.message
                    else -> ""
                },
                visible = snackVisible,
                accessibility = SKAccessibilityConfig(testTag = "consumer_form_snack"),
            )
        },
        contentSafeDrawing = false,
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
                    supportingText = "Host owns submit loading.",
                )
                SKTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = displayName,
                    onValueChange = { displayName = it },
                    fieldId = "displayName",
                    label = "Display name",
                    required = true,
                    enabled = !submitting,
                    rules = listOf(SKRequiredRule(), SKMinLengthRule(2)),
                    formatter = SKTrimFormatter,
                    imeAction = SKImeAction.Next,
                    accessibility = SKAccessibilityConfig(testTag = "consumer_form_name"),
                )
                SKTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    fieldId = "email",
                    label = "Email",
                    required = true,
                    enabled = !submitting,
                    rules = listOf(SKRequiredRule(), SKEmailRule()),
                    formatter = SKTrimFormatter,
                    keyboardType = SKKeyboardType.Email,
                    imeAction = SKImeAction.Next,
                    accessibility = SKAccessibilityConfig(testTag = "consumer_form_email"),
                )
                SKTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    fieldId = "title",
                    label = "Job title",
                    enabled = false,
                    accessibility = SKAccessibilityConfig(testTag = "consumer_form_title"),
                )
                SKSwitch(
                    checked = notifications,
                    onCheckedChange = { notifications = it },
                    enabled = !submitting,
                    label = "Email notifications",
                )
                SKCheckbox(
                    checked = marketing,
                    onCheckedChange = { marketing = it },
                    enabled = !submitting,
                    label = "Product updates",
                )
                if (submitting) {
                    SKProgressIndicator(
                        indeterminate = true,
                        accessibility = SKAccessibilityConfig(
                            contentDescription = "Saving",
                            testTag = "consumer_form_loading",
                        ),
                    )
                }
                SKCheckbox(
                    checked = forceFail,
                    onCheckedChange = { forceFail = it },
                    enabled = !submitting,
                    label = "Simulate server failure",
                )
                SKButton(
                    text = if (submitting) "Saving…" else "Save profile",
                    enabled = !submitting,
                    loading = submitting,
                    onClick = {
                        val validation = form.validate()
                        if (!validation.isValid || !ConsumerLogic.canSubmit(draft)) {
                            status = FormStatus.Failure("Fix validation errors before saving")
                            snackVisible = true
                        } else {
                            status = FormStatus.Submitting
                            snackVisible = false
                            scope.launch {
                                delay(700)
                                if (forceFail) {
                                    status = FormStatus.Failure("Server rejected the update")
                                    snackVisible = true
                                } else {
                                    form.submit()
                                    status = FormStatus.Success
                                    snackVisible = true
                                }
                            }
                        }
                    },
                    accessibility = SKAccessibilityConfig(testTag = "consumer_form_submit"),
                )
                SKButton(
                    text = "Discard changes",
                    enabled = !submitting,
                    onClick = { confirmDiscard = true },
                )
            }
        }
    }

    SKAlertDialog(
        visible = confirmDiscard,
        title = "Discard changes?",
        message = "Unsaved edits will be lost.",
        confirmLabel = "Discard",
        dismissLabel = "Keep editing",
        onConfirm = {
            displayName = ""
            email = ""
            marketing = false
            notifications = true
            status = FormStatus.Idle
            snackVisible = false
            form.reset()
            confirmDiscard = false
        },
        onDismissRequest = { confirmDiscard = false },
        accessibility = SKAccessibilityConfig(testTag = "consumer_form_discard_dialog"),
    )
}
