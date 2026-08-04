package io.skone.theme.tokens

/**
 * Semantic color tokens for SKOne.
 *
 * @see docs/DESIGN_SYSTEM.md
 */
public interface SKColorTokens {
    public val primary: SKColor
    public val onPrimary: SKColor
    public val primaryContainer: SKColor
    public val onPrimaryContainer: SKColor

    public val secondary: SKColor
    public val onSecondary: SKColor
    public val secondaryContainer: SKColor
    public val onSecondaryContainer: SKColor

    public val tertiary: SKColor
    public val onTertiary: SKColor
    public val tertiaryContainer: SKColor
    public val onTertiaryContainer: SKColor

    public val error: SKColor
    public val onError: SKColor
    public val errorContainer: SKColor
    public val onErrorContainer: SKColor

    public val background: SKColor
    public val onBackground: SKColor

    public val surface: SKColor
    public val onSurface: SKColor
    public val surfaceVariant: SKColor
    public val onSurfaceVariant: SKColor

    public val outline: SKColor
    public val outlineVariant: SKColor

    public val scrim: SKColor
    public val inverseSurface: SKColor
    public val inverseOnSurface: SKColor
    public val inversePrimary: SKColor
}
