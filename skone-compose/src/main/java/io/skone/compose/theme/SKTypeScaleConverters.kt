package io.skone.compose.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import io.skone.theme.tokens.SKTypeScale

/**
 * Maps an SKOne [SKTypeScale] token to Compose [TextStyle].
 *
 * Font family keys are resolved to [FontFamily.SansSerif] by default;
 * apps may override via a custom mapper later.
 */
public fun SKTypeScale.toTextStyle(
    fontFamily: FontFamily = FontFamily.SansSerif,
): TextStyle = TextStyle(
    fontFamily = fontFamily,
    fontWeight = FontWeight(weight.coerceIn(1, 1000)),
    fontSize = size.toSp(),
    lineHeight = lineHeight.toSp(),
    letterSpacing = letterSpacing.toSp(),
)
