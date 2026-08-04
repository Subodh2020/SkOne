package io.skone.compose.widget

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import io.skone.compose.theme.SKTheme
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.theme.SKThemeMode
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.text.SKAnnotatedText
import io.skone.ui.text.SKSpanStyle
import io.skone.ui.text.SKTextSpan

/**
 * Screenshot tests for [SKText] (reference widget).
 */
class SKTextScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
    )

    @Test
    fun plainBodyText() {
        paparazzi.snapshot {
            SKTheme(mode = SKThemeMode.Light) {
                SKText(text = "Hello SKOne")
            }
        }
    }

    @Test
    fun titlePrimary() {
        paparazzi.snapshot {
            SKTheme(mode = SKThemeMode.Light) {
                SKText(
                    text = "Design System",
                    appearance = SKAppearanceConfig.Text.copy(
                        typographyRole = SKTypographyRole.HeadlineSmall,
                        contentColorRole = SKColorRole.Primary,
                    ),
                )
            }
        }
    }

    @Test
    fun annotatedRichText() {
        paparazzi.snapshot {
            SKTheme(mode = SKThemeMode.Light) {
                SKText(
                    annotated = SKAnnotatedText(
                        text = "Bold and primary",
                        spans = listOf(
                            SKTextSpan(0, 4, listOf(SKSpanStyle.Bold)),
                            SKTextSpan(9, 16, listOf(SKSpanStyle.ColorRole(SKColorRole.Primary))),
                        ),
                    ),
                )
            }
        }
    }
}
