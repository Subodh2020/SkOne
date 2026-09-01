@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import androidx.core.view.ViewCompat
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.common.log.SKNoOpLogger
import io.skone.theme.SKThemes
import io.skone.xml.theme.SKThemeHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKButtonViewTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun setSkText_andClick() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKButtonView(context)
        var clicks = 0
        view.setSkText("Save")
        view.setOnSkClickListener { clicks++ }
        view.performClick()
        assertEquals(1, clicks)
        assertEquals("Save", view.contentDescription?.toString())
    }

    @Test
    fun loading_andDisabledBlockClicks() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKButtonView(context)
        var clicks = 0
        view.setSkText("Go")
        view.setOnSkClickListener { clicks++ }

        view.setLoading(true)
        view.performClick()
        assertEquals(0, clicks)

        view.setLoading(false)
        view.setButtonEnabled(false)
        view.performClick()
        assertEquals(0, clicks)

        view.setButtonEnabled(true)
        view.performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun accessibility_testTagAndLoadingState() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKButtonView(context)
        view.setSkText("Submit")
        view.setLoading(true)
        view.setAccessibility(
            SKAccessibilityConfig(
                contentDescription = "Submit form",
                testTag = "xml_submit",
            ),
        )
        assertEquals("xml_submit", view.tag)
        assertEquals("Submit form", view.contentDescription?.toString())
        assertEquals("Loading", ViewCompat.getStateDescription(view)?.toString())
    }

    @Test
    fun leadingIcon_decorativeByDefault() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKButtonView(context)
        view.setSkText("Mail")
        view.setLeadingIcon(SKIconKey("skone.icon.mail"))
        val leading = view.getChildAt(0)
        assertEquals(android.view.View.VISIBLE, leading.visibility)
        assertNull(leading.contentDescription)
    }

    @Test
    fun bind_attachesComponent() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKButtonView(context)
        view.setSkText("OK")
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        view.bind(runtime)
        assertTrue(view.component.isAttached)
        view.unbind()
        assertFalse(view.component.isAttached)
    }

    @Test
    fun appearance_variantsApply() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKButtonView(context)
        view.setAppearance(SKAppearanceConfig.ButtonOutlined)
        assertEquals(
            SKAppearanceConfig.ButtonOutlined.outlineColorRole,
            (view.component as io.skone.ui.button.SKButtonComponent).config.appearance.outlineColorRole,
        )
    }
}
