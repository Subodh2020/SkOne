package io.skone.xml.widget

import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.ui.overlay.SKSegmentItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SKSheetSegmentXmlTest {

    private val context get() = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun bottomSheet_showDismissAndActions() {
        val sheet = SKBottomSheetView(context)
        var primary = 0
        var dismissed = 0
        sheet.setAccessibility(SKAccessibilityConfig(testTag = "sheet"))
        sheet.setSheetTitle("Filters")
        sheet.setSheetContent(TextView(context).apply { text = "Options" })
        sheet.setPrimaryAction("Apply", true) { primary++ }
        sheet.setSecondaryAction("Locked", false) {}
        sheet.setOnDismissListener { dismissed++ }
        sheet.show()
        assertTrue(sheet.isShowing)
        sheet.dismiss()
        assertFalse(sheet.isShowing)
        assertTrue(dismissed >= 1)
        assertEquals(0, primary)
    }

    @Test
    fun segmented_selectionAndDisabled() {
        val view = SKSegmentedButtonView(context)
        var selected = ""
        view.setAccessibility(SKAccessibilityConfig(testTag = "seg"))
        view.setSegmentItems(
            listOf(
                SKSegmentItem("all", "All"),
                SKSegmentItem("mine", "Mine"),
                SKSegmentItem("off", "Off", enabled = false),
            ),
        )
        view.setSelectedSegmentId("all")
        view.setOnSelectListener { selected = it }
        view.findViewWithTag<View>("seg_mine")!!.performClick()
        assertEquals("mine", selected)
        assertEquals("mine", view.currentSelectedId)
        view.findViewWithTag<View>("seg_off")!!.performClick()
        assertEquals("mine", view.currentSelectedId)
    }
}
