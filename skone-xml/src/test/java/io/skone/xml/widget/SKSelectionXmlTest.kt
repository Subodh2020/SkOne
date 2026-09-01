@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import androidx.core.view.ViewCompat
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.SKThemes
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
class SKSelectionXmlTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun radioGroup_enforcesSingleSelection() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val group = SKRadioGroupView(context)
        val a = SKRadioButtonView(context).apply {
            setRadioValue("a")
            setLabel("Alpha")
        }
        val b = SKRadioButtonView(context).apply {
            setRadioValue("b")
            setLabel("Beta")
        }
        group.addView(a)
        group.addView(b)
        var last: String? = null
        group.setOnSelectedChangeListener { last = it }
        a.performClick()
        assertEquals("a", group.selectedValue)
        assertTrue(a.isSelectedState)
        assertFalse(b.isSelectedState)
        b.performClick()
        assertEquals("b", group.selectedValue)
        assertEquals("b", last)
        assertTrue(b.isSelectedState)
        assertFalse(a.isSelectedState)
    }

    @Test
    fun chip_selectedStateAndClick() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val chip = SKChipView(context)
        var clicks = 0
        chip.setSkLabel("Compose")
        chip.setLeadingIcon(SKIconKey("skone.icon.tag"))
        chip.setAccessibility(SKAccessibilityConfig(testTag = "xml_chip", contentDescription = "Compose chip"))
        chip.setOnSkClickListener { clicks++ }
        chip.performClick()
        assertEquals(1, clicks)
        chip.setSelectedState(true)
        assertTrue(chip.isSelectedState)
        assertEquals("xml_chip", chip.tag)
        assertEquals("Selected", ViewCompat.getStateDescription(chip)?.toString())
    }
}
