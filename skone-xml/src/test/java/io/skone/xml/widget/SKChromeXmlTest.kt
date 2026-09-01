package io.skone.xml.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.ui.chrome.SKTabItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SKChromeXmlTest {

    private val context get() = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun tabRow_selectionDisabledAndTestTag() {
        val view = SKTabRowView(context)
        var selected = ""
        view.setAccessibility(SKAccessibilityConfig(testTag = "tabs"))
        view.setTabItems(
            listOf(
                SKTabItem("a", "All"),
                SKTabItem("b", "Blocked", enabled = false),
                SKTabItem("c", "Starred"),
            ),
        )
        view.setSelectedTabId("a")
        view.setOnSelectListener { selected = it }
        view.findViewWithTag<View>("tabs_c")!!.performClick()
        assertEquals("c", selected)
        assertEquals("c", view.currentSelectedId)
        view.findViewWithTag<View>("tabs_b")!!.performClick()
        assertEquals("c", view.currentSelectedId)
        view.setControlEnabled(false)
        assertFalse(view.isEnabled)
    }

    @Test
    fun badge_countDotVisibilityAndA11y() {
        val view = SKBadgeView(context)
        view.setBadgeText("3")
        view.setAccessibility(SKAccessibilityConfig(testTag = "badge"))
        assertEquals("3", view.text.toString())
        assertEquals("3", view.contentDescription)
        view.setDot(true)
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, view.importantForAccessibility)
        view.setDot(false)
        view.setBadgeVisible(false)
        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun avatar_initialsImageAndCd() {
        val view = SKAvatarView(context)
        view.setInitials("ab")
        view.setAccessibility(SKAccessibilityConfig(testTag = "avatar"))
        assertEquals("AB", (view.getChildAt(0) as android.widget.TextView).text.toString())
        assertEquals("ab", view.contentDescription)
        view.setImage(ColorDrawable(Color.BLUE))
        assertEquals(View.VISIBLE, view.getChildAt(1).visibility)
        view.setAccessibility(SKAccessibilityConfig(contentDescription = "Profile", testTag = "avatar"))
        assertEquals("Profile", view.contentDescription)
        view.setInitials("")
        view.setImage(null)
        view.setAccessibility(SKAccessibilityConfig(testTag = "avatar"))
        assertNull(view.contentDescription)
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, view.importantForAccessibility)
    }
}
