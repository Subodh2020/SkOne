package io.skone.theme.defaults

import io.skone.theme.tokens.SKColor
import io.skone.theme.tokens.SKColorTokens

/**
 * Default light palette for SKOne.
 *
 * Values are seed defaults for the design system — apps override via [io.skone.theme.SKThemeBuilder].
 * Widgets must never hardcode these ARGB values; they resolve through [SKColorTokens].
 */
public data class SKLightColorTokens(
    override val primary: SKColor = SKColor(0xFF2F6FED.toInt()),
    override val onPrimary: SKColor = SKColor(0xFFFFFFFF.toInt()),
    override val primaryContainer: SKColor = SKColor(0xFFD9E4FF.toInt()),
    override val onPrimaryContainer: SKColor = SKColor(0xFF001A41.toInt()),
    override val secondary: SKColor = SKColor(0xFF555F71.toInt()),
    override val onSecondary: SKColor = SKColor(0xFFFFFFFF.toInt()),
    override val secondaryContainer: SKColor = SKColor(0xFFD9E3F8.toInt()),
    override val onSecondaryContainer: SKColor = SKColor(0xFF121C2B.toInt()),
    override val tertiary: SKColor = SKColor(0xFF6E5676.toInt()),
    override val onTertiary: SKColor = SKColor(0xFFFFFFFF.toInt()),
    override val tertiaryContainer: SKColor = SKColor(0xFFF7D8FF.toInt()),
    override val onTertiaryContainer: SKColor = SKColor(0xFF27132F.toInt()),
    override val error: SKColor = SKColor(0xFFBA1A1A.toInt()),
    override val onError: SKColor = SKColor(0xFFFFFFFF.toInt()),
    override val errorContainer: SKColor = SKColor(0xFFFFDAD6.toInt()),
    override val onErrorContainer: SKColor = SKColor(0xFF410002.toInt()),
    override val background: SKColor = SKColor(0xFFF8F9FF.toInt()),
    override val onBackground: SKColor = SKColor(0xFF191C20.toInt()),
    override val surface: SKColor = SKColor(0xFFF8F9FF.toInt()),
    override val onSurface: SKColor = SKColor(0xFF191C20.toInt()),
    override val surfaceVariant: SKColor = SKColor(0xFFE0E2EC.toInt()),
    override val onSurfaceVariant: SKColor = SKColor(0xFF43474E.toInt()),
    override val outline: SKColor = SKColor(0xFF74777F.toInt()),
    override val outlineVariant: SKColor = SKColor(0xFFC4C6D0.toInt()),
    override val scrim: SKColor = SKColor(0xFF000000.toInt()),
    override val inverseSurface: SKColor = SKColor(0xFF2E3035.toInt()),
    override val inverseOnSurface: SKColor = SKColor(0xFFEFF0F7.toInt()),
    override val inversePrimary: SKColor = SKColor(0xFFAFC6FF.toInt()),
) : SKColorTokens

/**
 * Default dark palette for SKOne.
 */
public data class SKDarkColorTokens(
    override val primary: SKColor = SKColor(0xFFAFC6FF.toInt()),
    override val onPrimary: SKColor = SKColor(0xFF002E69.toInt()),
    override val primaryContainer: SKColor = SKColor(0xFF0E458F.toInt()),
    override val onPrimaryContainer: SKColor = SKColor(0xFFD9E4FF.toInt()),
    override val secondary: SKColor = SKColor(0xFFBDC7DC.toInt()),
    override val onSecondary: SKColor = SKColor(0xFF273141.toInt()),
    override val secondaryContainer: SKColor = SKColor(0xFF3E4758.toInt()),
    override val onSecondaryContainer: SKColor = SKColor(0xFFD9E3F8.toInt()),
    override val tertiary: SKColor = SKColor(0xFFDABCE2.toInt()),
    override val onTertiary: SKColor = SKColor(0xFF3E2846.toInt()),
    override val tertiaryContainer: SKColor = SKColor(0xFF563E5D.toInt()),
    override val onTertiaryContainer: SKColor = SKColor(0xFFF7D8FF.toInt()),
    override val error: SKColor = SKColor(0xFFFFB4AB.toInt()),
    override val onError: SKColor = SKColor(0xFF690005.toInt()),
    override val errorContainer: SKColor = SKColor(0xFF93000A.toInt()),
    override val onErrorContainer: SKColor = SKColor(0xFFFFDAD6.toInt()),
    override val background: SKColor = SKColor(0xFF111318.toInt()),
    override val onBackground: SKColor = SKColor(0xFFE1E2E9.toInt()),
    override val surface: SKColor = SKColor(0xFF111318.toInt()),
    override val onSurface: SKColor = SKColor(0xFFE1E2E9.toInt()),
    override val surfaceVariant: SKColor = SKColor(0xFF43474E.toInt()),
    override val onSurfaceVariant: SKColor = SKColor(0xFFC4C6D0.toInt()),
    override val outline: SKColor = SKColor(0xFF8E9199.toInt()),
    override val outlineVariant: SKColor = SKColor(0xFF43474E.toInt()),
    override val scrim: SKColor = SKColor(0xFF000000.toInt()),
    override val inverseSurface: SKColor = SKColor(0xFFE1E2E9.toInt()),
    override val inverseOnSurface: SKColor = SKColor(0xFF2E3035.toInt()),
    override val inversePrimary: SKColor = SKColor(0xFF2F6FED.toInt()),
) : SKColorTokens
