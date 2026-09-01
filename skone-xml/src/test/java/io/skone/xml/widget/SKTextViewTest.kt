@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import androidx.core.view.ViewCompat
import androidx.test.core.app.ApplicationProvider
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.common.log.SKNoOpLogger
import io.skone.theme.SKThemes
import io.skone.theme.tokens.SKColorRole
import io.skone.ui.text.SKAnnotatedText
import io.skone.ui.text.SKSpanStyle
import io.skone.ui.text.SKTextSpan
import io.skone.xml.theme.SKThemeHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKTextViewTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun setSkText_updatesContentAndDescription() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextView(context)
        view.setSkText("Hello XML")

        assertEquals("Hello XML", view.text.toString())
        assertEquals("Hello XML", view.contentDescription?.toString())
    }

    @Test
    fun bind_attachesComponent() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextView(context)
        view.setSkText("Bound")
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        view.bind(runtime)

        assertTrue(view.component.isAttached)
        view.unbind()
        assertTrue(!view.component.isAttached)
    }

    @Test
    fun annotatedText_appliesSpans() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextView(context)
        view.setSkAnnotated(
            SKAnnotatedText(
                text = "Bold text",
                spans = listOf(SKTextSpan(0, 4, listOf(SKSpanStyle.Bold))),
            ),
        )
        assertTrue(view.text.toString().startsWith("Bold"))
    }

    @Test
    fun colorRoleSpan_doesNotThrow() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextView(context)
        view.setSkAnnotated(
            SKAnnotatedText(
                text = "Primary accent",
                spans = listOf(
                    SKTextSpan(0, 7, listOf(SKSpanStyle.ColorRole(SKColorRole.Primary))),
                ),
            ),
        )
        assertEquals("Primary accent", view.text.toString())
    }

    @Test
    fun accessibility_appliesTestTagHeadingAndStateDescription() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextView(context)
        view.setSkText("Title")
        view.setAccessibility(
            SKAccessibilityConfig(
                contentDescription = "Section title",
                testTag = "sktext_xml_title",
                stateDescription = "Expanded",
                heading = true,
            ),
        )

        assertEquals("sktext_xml_title", view.tag)
        assertEquals("Section title", view.contentDescription?.toString())
        assertEquals("Expanded", ViewCompat.getStateDescription(view)?.toString())
        assertTrue(ViewCompat.isAccessibilityHeading(view))
    }
}
