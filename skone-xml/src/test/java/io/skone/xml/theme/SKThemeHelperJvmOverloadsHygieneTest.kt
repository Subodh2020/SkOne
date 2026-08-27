package io.skone.xml.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.skone.theme.SKThemeMode
import io.skone.theme.SKThemes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Confirms Java-friendly [@JvmOverloads] install paths after provider default hygiene.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKThemeHelperJvmOverloadsHygieneTest {

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun installContextOnlyUsesLibraryDefaultProvider() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Java callers use install(Context) / install(Context, mode) overloads.
        SKThemeHelper.install(context)
        assertEquals(SKThemes.Light.name, SKThemeHelper.current().name)
    }

    @Test
    fun installContextAndModeUsesLibraryDefaultProvider() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        SKThemeHelper.install(context, SKThemeMode.Dark)
        assertEquals(SKThemes.Dark.name, SKThemeHelper.current().name)
    }
}
