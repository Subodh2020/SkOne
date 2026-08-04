package io.skone.compose.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.skone.theme.shape.SKShape
import io.skone.theme.tokens.SKColor
import io.skone.theme.tokens.SKDp
import io.skone.theme.tokens.SKDuration
import io.skone.theme.tokens.SKSp

/**
 * Converts an SKOne [SKColor] token to a Compose [Color].
 */
public fun SKColor.toColor(): Color = Color(argb)

/**
 * Converts an SKOne [SKDp] token to Compose [Dp].
 */
public fun SKDp.toDp(): Dp = value.dp

/**
 * Converts an SKOne [SKSp] token to Compose [TextUnit].
 */
public fun SKSp.toSp(): TextUnit = value.sp

/**
 * Converts an SKOne [SKDuration] to milliseconds as [Int] for Compose animations.
 */
public fun SKDuration.toIntMillis(): Int = millis.coerceIn(0, Int.MAX_VALUE.toLong()).toInt()

/**
 * Maps an SKOne [SKShape] to a Compose [Shape].
 */
public fun SKShape.toComposeShape(): Shape = when (this) {
    SKShape.Rectangle -> RectangleShape
    SKShape.Circle -> CircleShape
    is SKShape.Rounded -> RoundedCornerShape(radius.toDp())
}
