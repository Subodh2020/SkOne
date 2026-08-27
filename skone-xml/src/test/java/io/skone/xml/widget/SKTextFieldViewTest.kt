@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import androidx.test.core.app.ApplicationProvider
import io.skone.component.framework.SKComponentRuntime
import io.skone.common.log.SKNoOpLogger
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemes
import io.skone.ui.field.SKFieldVisualState
import io.skone.xml.theme.SKThemeHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
}
