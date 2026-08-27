package io.skone.xml.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.skone.common.annotation.SKInternal
import io.skone.theme.SKThemes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Annotation hygiene: [SKThemeHelper.install] / [SKThemeHelper.current] stay OptIn-free.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKThemeHelperAnnotationHygieneTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @After
    @OptIn(SKInternal::class)
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun installAndCurrentDoNotRequireOptIn() {
        SKThemeHelper.install(SKThemes.Light)
        assertEquals(SKThemes.Light.name, SKThemeHelper.current().name)
        SKThemeHelper.install(context)
        assertEquals(SKThemes.Light.name, SKThemeHelper.current().name)
    }

    @Test
    @OptIn(SKInternal::class)
    fun clearIsCallableWithOptIn() {
        SKThemeHelper.install(SKThemes.Dark)
        SKThemeHelper.clear()
        assertEquals(SKThemes.Light.name, SKThemeHelper.current().name)
    }
}
