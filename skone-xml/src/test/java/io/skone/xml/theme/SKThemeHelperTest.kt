@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.skone.theme.SKThemeMode
import io.skone.theme.SKThemeProvider
import io.skone.theme.SKThemes
import io.skone.theme.SKTheme as SKThemeModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Public contracts for [SKThemeHelper].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKThemeHelperTest {

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun currentFallsBackToLightWhenNotInstalled() {
        SKThemeHelper.clear()
        assertEquals(SKThemes.Light.name, SKThemeHelper.current().name)
        assertEquals(SKThemeMode.Light, SKThemeHelper.current().mode)
    }

    @Test
    fun installThemeUpdatesCurrent() {
        SKThemeHelper.install(SKThemes.Dark)
        assertEquals(SKThemes.Dark.name, SKThemeHelper.current().name)
        assertEquals(SKThemes.Dark.tokens.colors.primary, SKThemeHelper.current().tokens.colors.primary)
    }

    @Test
    fun installContextModeResolvesLightAndDark() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        SKThemeHelper.install(context, mode = SKThemeMode.Light)
        assertEquals(SKThemes.Light.name, SKThemeHelper.current().name)

        SKThemeHelper.install(context, mode = SKThemeMode.Dark)
        assertEquals(SKThemes.Dark.name, SKThemeHelper.current().name)
    }

    @Test
    fun installContextUsesCustomProvider() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val provider = object : SKThemeProvider {
            override fun theme(mode: SKThemeMode, isSystemInDarkTheme: Boolean): SKThemeModel =
                SKThemes.Light.copy(name = "skone.xml.provider")
        }
        SKThemeHelper.install(context, mode = SKThemeMode.Dark, provider = provider)
        assertEquals("skone.xml.provider", SKThemeHelper.current().name)
    }

    @Test
    fun requireThrowsWhenNotInstalled() {
        SKThemeHelper.clear()
        val error = assertThrows(IllegalStateException::class.java) {
            SKThemeHelper.require()
        }
        assertTrue(error.message.orEmpty().contains("SKThemeHelper is not installed"))
    }

    @Test
    fun requireReturnsInstalledTheme() {
        SKThemeHelper.install(SKThemes.Dark)
        assertEquals(SKThemes.Dark.name, SKThemeHelper.require().name)
    }

    @Test
    fun clearResetsToLightFallback() {
        SKThemeHelper.install(SKThemes.Dark)
        SKThemeHelper.clear()
        assertEquals(SKThemes.Light.name, SKThemeHelper.current().name)
    }
}
