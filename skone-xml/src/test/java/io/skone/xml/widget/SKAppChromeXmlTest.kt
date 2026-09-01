package io.skone.xml.widget

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SKAppChromeXmlTest {

    private val context get() = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun searchBar_queryClearImeAndDisabled() {
        val view = SKSearchBarView(context)
        var last = ""
        var cleared = false
        var searched = ""
        view.setOnQueryChangeListener { last = it }
        view.setOnClearListener { cleared = true }
        view.setOnSearchListener { searched = it }
        view.setAccessibility(SKAccessibilityConfig(testTag = "search"))
        view.setQuery("android")
        assertEquals("android", view.input.text.toString())
        // User typing path fires the listener
        view.input.setText("typed")
        assertEquals("typed", last)
        assertEquals(
            EditorInfo.IME_ACTION_SEARCH,
            view.input.imeOptions and EditorInfo.IME_MASK_ACTION,
        )
        view.findViewWithTag<View>("search_clear")!!.performClick()
        assertTrue(cleared)
        assertEquals("", view.input.text.toString())
        assertEquals("", last)
        view.setQuery("compose")
        view.input.onEditorAction(EditorInfo.IME_ACTION_SEARCH)
        assertEquals("compose", searched)
        view.setControlEnabled(false)
        assertFalse(view.input.isEnabled)
    }

    @Test
    fun emptyState_rendersActionsAndLongTitle() {
        val view = SKEmptyStateView(context)
        var primary = 0
        view.setTitle("Nothing here yet with a fairly long title for truncation hosts")
        view.setDescription("Try adjusting filters")
        view.setIcon(SKIconKey("skone.icon.empty"))
        view.setPrimaryAction("Reset") { primary++ }
        view.setSecondaryAction("Help") {}
        view.setAccessibility(SKAccessibilityConfig(testTag = "empty"))
        assertEquals(
            "Nothing here yet with a fairly long title for truncation hosts. Try adjusting filters",
            view.contentDescription,
        )
        view.findViewWithTag<View>("empty_primary")!!.performClick()
        assertEquals(1, primary)
    }

    @Test
    fun fab_clickDisabledAndTestTag() {
        val view = SKFabView(context)
        var clicks = 0
        view.setIcon(SKIconKey("skone.icon.add", contentDescription = "Create"))
        view.setAccessibility(SKAccessibilityConfig(contentDescription = "Create", testTag = "fab"))
        view.setOnFabClickListener { clicks++ }
        view.performClick()
        assertEquals(1, clicks)
        assertEquals("fab", view.tag)
        view.setControlEnabled(false)
        view.performClick()
        assertEquals(1, clicks)
    }
}
