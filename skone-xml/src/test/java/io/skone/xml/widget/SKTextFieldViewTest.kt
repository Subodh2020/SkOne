@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import androidx.core.view.ViewCompat
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.common.log.SKNoOpLogger
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemes
import io.skone.ui.field.SKFieldVisualState
import io.skone.xml.theme.SKThemeHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKTextFieldViewTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun setSkValue_updatesEditText() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Email")
        view.setSkValue("hello")

        assertEquals("hello", view.getSkValue())
        assertEquals("Email", view.contentDescription?.toString())
    }

    @Test
    fun bind_attachesComponentAndRegistersForm() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setFieldId("email")
        view.setLabel("Email")
        view.setRules(listOf(SKRequiredRule()))
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        val form = SKFormController.create()
        view.bind(runtime, form)

        assertTrue(view.component.isAttached)
        assertNotNull(form.registry.get("email"))

        view.unbind()
        assertTrue(!view.component.isAttached)
        assertEquals(null, form.registry.get("email"))
        form.dispose()
    }

    @Test
    fun visualState_errorUpdatesSupportingText() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setSupportingText("Help")
        view.setVisualState(SKFieldVisualState.Error)
        view.applyValidationResult(
            io.skone.component.validation.SKValidationResult.Invalid(
                io.skone.component.validation.SKValidationError("x", "Invalid value"),
            ),
        )
        assertEquals(SKFieldVisualState.Error, (view.component as io.skone.ui.field.SKTextFieldComponent).visualState)
    }

    @Test
    fun accessibility_usesLabelAsDescription() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Phone")
        view.setHint("555")
        assertEquals("Phone", view.contentDescription?.toString())
        assertEquals("Phone", view.input.contentDescription?.toString())
    }

    @Test
    fun accessibility_testTagLandsOnPrimaryInput() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Email")
        // Near-miss tag on root must not be required; primary editable owns the test tag.
        view.setAccessibility(
            SKAccessibilityConfig(
                contentDescription = "Work email",
                testTag = "xml_email_primary",
                stateDescription = "Needs review",
                heading = true,
            ),
        )

        assertEquals("xml_email_primary", view.input.tag)
        assertEquals("Work email", view.input.contentDescription?.toString())
        assertEquals("Needs review", ViewCompat.getStateDescription(view.input)?.toString())
        assertTrue(ViewCompat.isAccessibilityHeading(view.input))
        // Root keeps a CD for hosts but must not steal the automation tag.
        assertEquals("Work email", view.contentDescription?.toString())
        assertTrue(view.tag == null || view.tag != "xml_email_primary")
    }

    @Test
    fun accessibility_requiredMergesStateDescriptionWithoutRewritingCd() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Name")
        view.setRequired(true)
        view.setAccessibility(
            SKAccessibilityConfig(
                contentDescription = "Full name",
                stateDescription = "Incomplete",
            ),
        )

        assertEquals("Full name", view.input.contentDescription?.toString())
        assertEquals("Incomplete, Required", ViewCompat.getStateDescription(view.input)?.toString())
    }

    @Test
    fun accessibility_errorExposesNodeInfoError() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Email")
        view.setSupportingText("Bad address")
        view.setVisualState(SKFieldVisualState.Error)

        val info = view.input.createAccessibilityNodeInfo()
        assertTrue(info.isContentInvalid)
        @Suppress("DEPRECATION")
        assertEquals("Bad address", info.error?.toString())
        info.recycle()
    }

    @Test
    fun leadingIcon_rendersDecorativelyByDefault() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Email")
        view.setLeadingIcon(SKIconKey("skone.icon.mail"))

        // Icon row child becomes visible; raw key must not become contentDescription.
        val leading = (view.getChildAt(1) as android.widget.LinearLayout).getChildAt(0)
        assertEquals(android.view.View.VISIBLE, leading.visibility)
        assertNull(leading.contentDescription)
        assertEquals(
            android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO,
            leading.importantForAccessibility,
        )
    }

    @Test
    fun leadingIcon_explicitCdIsAnnounced() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLeadingIcon(
            SKIconKey(key = "skone.icon.mail", contentDescription = "Mailbox"),
        )
        val leading = (view.getChildAt(1) as android.widget.LinearLayout).getChildAt(0)
        assertEquals("Mailbox", leading.contentDescription?.toString())
        assertFalse(leading.contentDescription?.toString() == "skone.icon.mail")
    }
}
