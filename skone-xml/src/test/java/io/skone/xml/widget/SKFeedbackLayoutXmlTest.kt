@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.view.View
import androidx.core.view.ViewCompat
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.theme.SKThemes
import io.skone.ui.feedback.SKProgressStyle
import io.skone.ui.layout.SKDividerOrientation
import io.skone.xml.theme.SKThemeHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKFeedbackLayoutXmlTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun slider_updatesValueOnTouchSimulation() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val slider = SKSliderView(context)
        slider.setValueRange(0f, 100f)
        slider.setSteps(0)
        var last = -1f
        slider.setOnValueChangeListener { last = it }
        slider.setSliderValue(25f)
        assertEquals(25f, slider.value, 0.01f)
        slider.setControlEnabled(false)
        assertFalse(slider.isEnabled)
        slider.setAccessibility(SKAccessibilityConfig(testTag = "xml_slider"))
        assertEquals("xml_slider", slider.tag)
        assertTrue(ViewCompat.getStateDescription(slider)?.toString()?.isNotBlank() == true)
        assertEquals(-1f, last, 0.01f)
    }

    @Test
    fun progress_determinateAndIndeterminate() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKProgressIndicatorView(context)
        view.setProgressFraction(0.4f)
        assertEquals(0.4f, view.progressFraction, 0.01f)
        view.setProgressStyle(SKProgressStyle.Circular)
        view.setIndeterminateMode(true)
        assertTrue(view.isIndeterminate)
        view.setAccessibility(SKAccessibilityConfig(testTag = "xml_progress", contentDescription = "Upload"))
        assertEquals("xml_progress", view.tag)
    }

    @Test
    fun divider_isDecorative() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val divider = SKDividerView(context)
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, divider.importantForAccessibility)
        divider.setDividerOrientation(SKDividerOrientation.Vertical)
    }

    @Test
    fun card_clickable() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val card = SKCardView(context)
        var clicks = 0
        card.setOnSkClickListener { clicks++ }
        assertTrue(card.isClickable)
        card.performClick()
        assertEquals(1, clicks)
        card.setControlEnabled(false)
        assertFalse(card.isEnabled)
    }
}
