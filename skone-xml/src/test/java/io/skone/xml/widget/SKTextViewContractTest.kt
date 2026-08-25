package io.skone.xml.widget

import androidx.test.core.app.ApplicationProvider
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.theme.SKThemes
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.color
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.toArgb
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Additional public contracts for [SKTextView] beyond bind/text smoke coverage.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKTextViewContractTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun setSkTextUpdatesReplacePreviousContent() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextView(context)
        view.setSkText("First")
        assertEquals("First", view.text.toString())

        view.setSkText("Second")
        assertEquals("Second", view.text.toString())
        assertEquals("Second", view.contentDescription?.toString())
    }

    @Test
    fun appearancePrimaryColorUsesInstalledThemeTokens() {
        assertNotEquals(
            SKThemes.Light.tokens.colors.primary,
            SKThemes.Dark.tokens.colors.primary,
        )

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextView(context)
        view.setAppearance(
            SKAppearanceConfig.Text.copy(contentColorRole = SKColorRole.Primary),
        )
        view.setSkText("Themed")

        assertEquals(
            SKThemes.Light.tokens.colors.color(SKColorRole.Primary).toArgb(),
            view.currentTextColor,
        )

        SKThemeHelper.install(SKThemes.Dark)
        view.setSkText("Themed")

        assertEquals(
            SKThemes.Dark.tokens.colors.color(SKColorRole.Primary).toArgb(),
            view.currentTextColor,
        )
        assertNotEquals(
            SKThemes.Light.tokens.colors.color(SKColorRole.Primary).toArgb(),
            view.currentTextColor,
        )
    }
}
