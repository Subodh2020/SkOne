@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import androidx.core.view.ViewCompat
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
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
class SKToggleXmlTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun checkbox_togglesAndExposesState() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKCheckboxView(context)
        var last: Boolean? = null
        view.setLabel("Accept")
        view.setOnCheckedChangeListener { last = it }
        view.setAccessibility(SKAccessibilityConfig(testTag = "xml_cb", contentDescription = "Accept"))
        assertFalse(view.isChecked)
        view.performClick()
        assertTrue(view.isChecked)
        assertEquals(true, last)
        assertEquals("xml_cb", view.tag)
        assertEquals("Checked", ViewCompat.getStateDescription(view)?.toString())
    }

    @Test
    fun switch_disabledBlocksToggle() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKSwitchView(context)
        view.setLabel("Wi‑Fi")
        view.setControlEnabled(false)
        view.performClick()
        assertFalse(view.isChecked)
    }

    @Test
    fun iconButton_usesExplicitCdNotRawKey() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKIconButtonView(context)
        view.setIcon(SKIconKey("skone.icon.menu", contentDescription = "Open menu"))
        view.setAccessibility(SKAccessibilityConfig(testTag = "xml_icon"))
        assertEquals("Open menu", view.contentDescription?.toString())
        assertNull(if (view.contentDescription?.toString() == "skone.icon.menu") "leak" else null)
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        view.bind(runtime)
        assertTrue(view.component.isAttached)
        view.unbind()
    }
}
