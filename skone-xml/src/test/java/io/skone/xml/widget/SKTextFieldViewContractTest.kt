package io.skone.xml.widget

import androidx.test.core.app.ApplicationProvider
import io.skone.theme.SKThemes
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.color
import io.skone.ui.field.SKFieldVisualState
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.toArgb
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Additional public contracts for [SKTextFieldView] beyond bind/value smoke coverage.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SKTextFieldViewContractTest {

    @Before
    fun setUp() {
        SKThemeHelper.install(SKThemes.Light)
    }

    @After
    fun tearDown() {
        SKThemeHelper.clear()
    }

    @Test
    fun hintAndLabelAreAppliedToChildViews() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Email")
        view.setHint("name@company.com")

        assertEquals("Email", childLabel(view).text.toString())
        assertEquals("name@company.com", view.input.hint?.toString())
        assertEquals(android.view.View.VISIBLE, childLabel(view).visibility)
    }

    @Test
    fun requiredAppendsAsteriskToLabel() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Email")
        view.setRequired(true)

        assertEquals("Email *", childLabel(view).text.toString())
    }

    @Test
    fun supportingTextIsShown() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setSupportingText("We'll never share your email.")

        val support = childSupporting(view)
        assertEquals(android.view.View.VISIBLE, support.visibility)
        assertEquals("We'll never share your email.", support.text.toString())
    }

    @Test
    fun disabledFieldDisablesInput() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setSkValue("keep-me")
        view.setFieldEnabled(false)

        assertFalse(view.input.isEnabled)
        assertFalse(view.input.isFocusable)
        assertEquals("keep-me", view.getSkValue())
    }

    @Test
    fun readOnlyFieldBlocksFocusAndCursor() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setSkValue("readonly@skone.io")
        view.setReadOnly(true)

        assertTrue(view.input.isEnabled)
        assertFalse(view.input.isFocusable)
        assertFalse(view.input.isFocusableInTouchMode)
        assertFalse(view.input.isCursorVisible)
        assertEquals("readonly@skone.io", view.getSkValue())
    }

    @Test
    fun valueChangeListenerReceivesUserEdits() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        val seen = mutableListOf<String>()
        view.setOnValueChangeListener { seen += it }

        view.input.setText("typed")

        assertTrue(seen.contains("typed"))
        assertEquals("typed", view.getSkValue())
    }

    @Test
    fun validationErrorUpdatesSupportingTextColorFromTheme() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setSupportingText("Help")
        view.applyValidationResult(
            io.skone.component.validation.SKValidationResult.Invalid(
                io.skone.component.validation.SKValidationError("email", "Invalid email"),
            ),
        )

        assertEquals(SKFieldVisualState.Error, (view.component as io.skone.ui.field.SKTextFieldComponent).visualState)
        assertEquals("Invalid email", childSupporting(view).text.toString())
        assertEquals(
            SKThemes.Light.tokens.colors.color(SKColorRole.Error).toArgb(),
            childSupporting(view).currentTextColor,
        )
    }

    @Test
    fun themeSwitchUpdatesLabelColorFromTokens() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val view = SKTextFieldView(context)
        view.setLabel("Email")

        val lightColor = SKThemes.Light.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb()
        assertEquals(lightColor, childLabel(view).currentTextColor)

        SKThemeHelper.install(SKThemes.Dark)
        view.setLabel("Email") // re-render with new theme

        val darkColor = SKThemes.Dark.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb()
        assertEquals(darkColor, childLabel(view).currentTextColor)
    }

    private fun childLabel(view: SKTextFieldView): android.widget.TextView =
        view.getChildAt(0) as android.widget.TextView

    private fun childSupporting(view: SKTextFieldView): android.widget.TextView =
        view.getChildAt(2) as android.widget.TextView
}
