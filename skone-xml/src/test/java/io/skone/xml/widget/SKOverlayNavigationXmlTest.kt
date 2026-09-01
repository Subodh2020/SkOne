@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.SKThemes
import io.skone.ui.navigation.SKNavigationItem
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
class SKOverlayNavigationXmlTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun snackbar_messageAndAction() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val bar = SKSnackbarView(context)
        var actions = 0
        bar.setMessage("Saved")
        bar.setActionLabel("Undo")
        bar.setOnActionListener { actions++ }
        bar.setAccessibility(SKAccessibilityConfig(testTag = "xml_snack"))
        assertEquals("xml_snack", bar.tag)
        (bar.getChildAt(1) as TextView).performClick()
        assertEquals(1, actions)
        bar.setSnackbarVisible(false)
        assertEquals(View.GONE, bar.visibility)
    }

    @Test
    fun alertDialog_confirm() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val host = SKAlertDialogHost(context)
            .setTitle("Delete?")
            .setMessage("Gone forever")
            .setDismissLabel(null)
            .setAccessibility(SKAccessibilityConfig(testTag = "xml_alert"))
        host.show()
        val component = host.skComponent as io.skone.ui.overlay.SKAlertDialogComponent
        assertTrue(component.visible)
        component.confirm()
        assertFalse(component.visible)
        host.dismiss()
    }

    @Test
    fun topAppBar_requiresIconCd() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val bar = SKTopAppBarView(context)
        bar.setBarTitle("Home")
        var threw = false
        try {
            bar.setNavigationIcon(SKIconKey("skone.icon.back"), onClick = {})
        } catch (_: IllegalArgumentException) {
            threw = true
        }
        assertTrue(threw)
        bar.setNavigationIcon(SKIconKey("skone.icon.back", contentDescription = "Back"), onClick = {})
    }

    @Test
    fun navigationBar_selection() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val nav = SKNavigationBarView(context)
        var selected: String? = null
        nav.setNavigationItems(
            listOf(SKNavigationItem("a", "Alpha"), SKNavigationItem("b", "Beta")),
        )
        nav.setOnSelectListener { selected = it }
        nav.setSelectedItemId("a")
        assertEquals("a", nav.currentSelectedId)
        nav.findViewWithTag<TextView>("nav_b")?.performClick()
        assertEquals("b", selected)
        nav.setControlEnabled(false)
        assertFalse(nav.isEnabled)
    }
}
