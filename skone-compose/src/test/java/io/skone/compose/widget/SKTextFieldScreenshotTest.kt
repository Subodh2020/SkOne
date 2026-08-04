package io.skone.compose.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.theme.SKTheme
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.theme.SKThemeMode
import org.junit.Rule
import org.junit.Test

/**
 * Screenshot tests for [SKTextField] (flagship input).
 */
class SKTextFieldScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
    )

    @Test
    fun emptyWithLabelAndHint() {
        paparazzi.snapshot {
            SKTheme(mode = SKThemeMode.Light) {
                FieldPadding {
                    SKTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = "",
                        onValueChange = {},
                        fieldId = "email",
                        label = "Email",
                        hint = "name@company.com",
                        supportingText = "We'll never share your email.",
                    )
                }
            }
        }
    }

    @Test
    fun filledDefault() {
        paparazzi.snapshot {
            SKTheme(mode = SKThemeMode.Light) {
                FieldPadding {
                    SKTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = "user@skone.io",
                        onValueChange = {},
                        fieldId = "email",
                        label = "Email",
                        required = true,
                    )
                }
            }
        }
    }

    @Test
    fun errorAppearance() {
        paparazzi.snapshot {
            SKTheme(mode = SKThemeMode.Light) {
                FieldPadding {
                    SKTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = "bad",
                        onValueChange = {},
                        fieldId = "email",
                        label = "Email",
                        supportingText = "Enter a valid email",
                        appearance = SKAppearanceConfig.TextFieldError,
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldPadding(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.padding(skTheme.tokens.spacing.md.toDp()),
    ) {
        content()
    }
}
