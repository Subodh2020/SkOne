package io.skone.consumer.xml

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.consumer.ConsumerLogic
import io.skone.consumer.FormStatus
import io.skone.consumer.ProfileDraft
import io.skone.forms.SKFormController
import io.skone.forms.formatter.SKTrimFormatter
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKMinLengthRule
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemes
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.widget.SKAlertDialogHost
import io.skone.xml.widget.SKButtonView
import io.skone.xml.widget.SKCheckboxView
import io.skone.xml.widget.SKProgressIndicatorView
import io.skone.xml.widget.SKScaffoldView
import io.skone.xml.widget.SKSectionHeaderView
import io.skone.xml.widget.SKSnackbarView
import io.skone.xml.widget.SKSwitchView
import io.skone.xml.widget.SKTextFieldView
import io.skone.xml.widget.SKTopAppBarView

/**
 * XML consumer path B — Form + Validation.
 * Host owns submit loading / success / failure; SKFormController validates fields.
 */
class XmlFormActivity : AppCompatActivity() {
    private val runtime = SKComponentRuntime.create()
    private val form = SKFormController.create()
    private var status: FormStatus = FormStatus.Idle
    private var forceFail = false
    private var notifications = true
    private var marketing = false

    private lateinit var nameField: SKTextFieldView
    private lateinit var emailField: SKTextFieldView
    private lateinit var submit: SKButtonView
    private lateinit var loading: SKProgressIndicatorView
    private lateinit var snack: SKSnackbarView
    private lateinit var discardDialog: SKAlertDialogHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SKThemeHelper.install(SKThemes.Light)

        val scaffold = SKScaffoldView(this)
        scaffold.topBarContainer.addView(
            SKTopAppBarView(this).apply {
                setBarTitle("Profile settings")
                setNavigationIcon(SKIconKey("skone.icon.back", contentDescription = "Back")) {
                    finish()
                }
                setAccessibility(SKAccessibilityConfig(testTag = "xml_form_topbar"))
                bind(runtime)
            },
        )

        val column = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        column.addView(
            SKSectionHeaderView(this).apply {
                setHeaderTitle("Account")
                setSupportingText("Host owns submit loading.")
                bind(runtime)
            },
        )

        nameField = SKTextFieldView(this).apply {
            setFieldId("displayName")
            setLabel("Display name")
            setRequired(true)
            setRules(listOf(SKRequiredRule(), SKMinLengthRule(2)))
            setFormatter(SKTrimFormatter)
            setAccessibility(SKAccessibilityConfig(testTag = "xml_form_name"))
            bind(runtime = runtime, form = form)
        }
        emailField = SKTextFieldView(this).apply {
            setFieldId("email")
            setLabel("Email")
            setRequired(true)
            setRules(listOf(SKRequiredRule(), SKEmailRule()))
            setFormatter(SKTrimFormatter)
            setAccessibility(SKAccessibilityConfig(testTag = "xml_form_email"))
            bind(runtime = runtime, form = form)
        }
        val titleField = SKTextFieldView(this).apply {
            setFieldId("title")
            setLabel("Job title")
            setSkValue("Engineer")
            setFieldEnabled(false)
            setAccessibility(SKAccessibilityConfig(testTag = "xml_form_title"))
            bind(runtime)
        }
        column.addView(nameField)
        column.addView(emailField)
        column.addView(titleField)

        column.addView(
            SKSwitchView(this).apply {
                setLabel("Email notifications")
                setChecked(notifications)
                setOnCheckedChangeListener { notifications = it }
                bind(runtime)
            },
        )
        column.addView(
            SKCheckboxView(this).apply {
                setLabel("Product updates")
                setChecked(marketing)
                setOnCheckedChangeListener { marketing = it }
                bind(runtime)
            },
        )
        column.addView(
            SKCheckboxView(this).apply {
                setLabel("Simulate server failure")
                setChecked(forceFail)
                setOnCheckedChangeListener { forceFail = it }
                bind(runtime)
            },
        )

        loading = SKProgressIndicatorView(this).apply {
            setIndeterminateMode(true)
            visibility = android.view.View.GONE
            setAccessibility(
                SKAccessibilityConfig(contentDescription = "Saving", testTag = "xml_form_loading"),
            )
            bind(runtime)
        }
        column.addView(loading)

        submit = SKButtonView(this).apply {
            setSkText("Save profile")
            setAccessibility(SKAccessibilityConfig(testTag = "xml_form_submit"))
            setOnSkClickListener { onSubmit() }
            bind(runtime)
        }
        column.addView(submit)

        discardDialog = SKAlertDialogHost(this)
            .setTitle("Discard changes?")
            .setMessage("Unsaved edits will be lost.")
            .setConfirmLabel("Discard")
            .setDismissLabel("Keep editing")
            .setAccessibility(SKAccessibilityConfig(testTag = "xml_form_discard_dialog"))
            .setOnConfirmListener {
                nameField.setSkValue("")
                emailField.setSkValue("")
                marketing = false
                notifications = true
                form.reset()
                status = FormStatus.Idle
                snack.setSnackbarVisible(false)
            }

        column.addView(
            SKButtonView(this).apply {
                setSkText("Discard changes")
                setOnSkClickListener { discardDialog.show() }
                bind(runtime)
            },
        )

        val scroll = ScrollView(this).apply {
            addView(
                column,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
        scaffold.contentContainer.addView(
            scroll,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        snack = SKSnackbarView(this).apply {
            setSnackbarVisible(false)
            setAccessibility(SKAccessibilityConfig(testTag = "xml_form_snack"))
            bind(runtime)
        }
        scaffold.snackbarContainer.addView(snack)

        setContentView(scaffold)
    }

    private fun onSubmit() {
        if (status is FormStatus.Submitting) return
        val draft = ProfileDraft(nameField.getSkValue(), emailField.getSkValue())
        val validation = form.validate()
        if (!validation.isValid || !ConsumerLogic.canSubmit(draft)) {
            status = FormStatus.Failure("Fix validation errors before saving")
            snack.setMessage("Fix validation errors before saving")
            snack.setSnackbarVisible(true)
            return
        }
        status = FormStatus.Submitting
        submit.setButtonEnabled(false)
        submit.setLoading(true)
        submit.setSkText("Saving…")
        loading.visibility = android.view.View.VISIBLE
        snack.setSnackbarVisible(false)

        window.decorView.postDelayed({
            if (forceFail) {
                status = FormStatus.Failure("Server rejected the update")
                snack.setMessage("Server rejected the update")
            } else {
                form.submit()
                status = FormStatus.Success
                snack.setMessage("Profile saved")
            }
            snack.setSnackbarVisible(true)
            submit.setButtonEnabled(true)
            submit.setLoading(false)
            submit.setSkText("Save profile")
            loading.visibility = android.view.View.GONE
        }, 700)
    }
}
