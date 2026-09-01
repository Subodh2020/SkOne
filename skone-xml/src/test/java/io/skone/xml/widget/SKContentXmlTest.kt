@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.view.View
import android.widget.TextView
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
class SKContentXmlTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun listItem_clickSelectedDisabled() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val row = SKListItemView(context)
        var clicks = 0
        row.setHeadline("Settings")
        row.setSupportingText("Account and privacy")
        row.setTrailingText(">")
        row.setLeadingIcon(SKIconKey("skone.icon.settings"))
        row.setOnSkClickListener { clicks++ }
        row.performClick()
        assertEquals(1, clicks)
        row.setSelectedState(true)
        assertTrue(row.isSelectedState)
        row.setControlEnabled(false)
        assertFalse(row.isEnabled)
        row.setAccessibility(SKAccessibilityConfig(testTag = "xml_list_item"))
        assertEquals("xml_list_item", row.tag)
    }

    @Test
    fun listItem_longHeadlineDoesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val row = SKListItemView(context)
        row.setHeadline("X".repeat(300))
        row.setSupportingText("Y".repeat(300))
        assertTrue(row.childCount > 0)
    }

    @Test
    fun sectionHeader_action() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val header = SKSectionHeaderView(context)
        var actions = 0
        header.setHeaderTitle("Recent")
        header.setActionLabel("See all")
        header.setOnActionListener { actions++ }
        header.setAccessibility(SKAccessibilityConfig(testTag = "xml_section"))
        assertEquals("xml_section", header.tag)
        assertTrue(ViewCompat.isAccessibilityHeading(header) || header.tag == "xml_section")
        (header.getChildAt(1) as TextView).performClick()
        assertEquals(1, actions)
    }

    @Test
    fun scaffold_slotsAndInsetsListener() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val scaffold = SKScaffoldView(context)
        val top = TextView(context).apply { text = "Top" }
        val content = TextView(context).apply { text = "Content" }
        val bottom = TextView(context).apply { text = "Bottom" }
        scaffold.setTopBar(top)
        scaffold.setContent(content)
        scaffold.setBottomBar(bottom)
        assertEquals(1, scaffold.topBarContainer.childCount)
        assertEquals(1, scaffold.contentContainer.childCount)
        assertEquals(1, scaffold.bottomBarContainer.childCount)
        scaffold.setTopBar(null)
        assertEquals(0, scaffold.topBarContainer.childCount)
        scaffold.setAccessibility(SKAccessibilityConfig(testTag = "xml_scaffold"))
        assertEquals("xml_scaffold", scaffold.tag)
    }

    @Test
    fun scaffold_multiChildChrome_requiresHostWrapper() {
        // FrameLayout slots accept multiple children but stack them (overlay).
        // Consumer apps must wrap stacked chrome in a vertical LinearLayout.
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val scaffold = SKScaffoldView(context)
        val wrapper = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(TextView(context).apply { text = "Bar" })
            addView(TextView(context).apply { text = "Tabs" })
        }
        scaffold.topBarContainer.addView(wrapper)
        assertEquals(1, scaffold.topBarContainer.childCount)
        assertEquals(2, (scaffold.topBarContainer.getChildAt(0) as android.widget.LinearLayout).childCount)
    }
}
