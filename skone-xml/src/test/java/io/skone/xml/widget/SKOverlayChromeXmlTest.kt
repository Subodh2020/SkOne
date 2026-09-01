package io.skone.xml.widget

import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.ui.overlay.SKMenuItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SKOverlayChromeXmlTest {

    private val context get() = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun menu_itemClickAndDisabled() {
        val view = SKMenuView(context)
        var clicked = ""
        view.setAccessibility(SKAccessibilityConfig(testTag = "menu"))
        view.setMenuItems(
            listOf(
                SKMenuItem("edit", "Edit"),
                SKMenuItem("delete", "Delete", enabled = false),
            ),
        )
        view.setOnItemClickListener { clicked = it }
        view.findViewWithTag<View>("menu_edit")!!.performClick()
        assertEquals("edit", clicked)
        view.findViewWithTag<View>("menu_delete")!!.performClick()
        assertEquals("edit", clicked)
        assertEquals("Menu", view.contentDescription)
    }

    @Test
    fun dropdown_showSelectDismiss() {
        val host = FrameLayout(context)
        val anchor = View(context)
        host.addView(anchor)
        val dropdown = SKDropdownMenuView(context)
        var selected = ""
        var dismissed = false
        dropdown.setAccessibility(SKAccessibilityConfig(testTag = "dd"))
        dropdown.setMenuItems(
            listOf(
                SKMenuItem("one", "One"),
                SKMenuItem("two", "Two", enabled = false),
            ),
        )
        dropdown.setOnSelectListener { selected = it }
        dropdown.setOnDismissListener { dismissed = true }
        dropdown.showAsDropDown(anchor)
        assertTrue(dropdown.expanded)
        // Popup content is not in the hierarchy of dropdown view; select via component API parity
        dropdown.component
        dropdown.setSelectedItemId("one")
        assertEquals("one", dropdown.currentSelectedId)
        dropdown.dismiss()
        assertFalse(dropdown.expanded)
        assertTrue(dismissed)
        assertEquals("", selected) // dismiss without item click
    }

    @Test
    fun tooltip_visibilityAndMessage() {
        val tip = SKTooltipView(context)
        tip.setMessage("Save draft")
        tip.setAccessibility(SKAccessibilityConfig(testTag = "tip"))
        tip.setTooltipVisible(false)
        assertEquals(View.GONE, tip.visibility)
        tip.setTooltipVisible(true)
        assertEquals(View.VISIBLE, tip.visibility)
        assertEquals("Save draft", tip.text.toString())
        assertEquals("Save draft", tip.contentDescription)
        assertEquals("tip", tip.tag)
    }

    @Test
    fun bottomAppBar_layoutAndTestTag() {
        val bar = SKBottomAppBarView(context)
        bar.setAccessibility(SKAccessibilityConfig(testTag = "bab", contentDescription = "Actions"))
        val leading = View(context)
        bar.setLeading(leading)
        assertEquals(1, bar.leadingContainer.childCount)
        assertEquals("Actions", bar.contentDescription)
        assertEquals("bab", bar.tag)
    }
}
