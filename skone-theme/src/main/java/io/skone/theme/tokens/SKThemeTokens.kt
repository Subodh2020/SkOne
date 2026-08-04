package io.skone.theme.tokens

/**
 * Aggregate design token surface for SKOne.
 *
 * Implementations may provide light/dark variants; UI bridges map these
 * contracts to Compose and XML without leaking framework types here.
 *
 * @see docs/DESIGN_SYSTEM.md
 * @see docs/adr/0005-theme-token-interfaces.md
 */
public interface SKThemeTokens {
    public val colors: SKColorTokens
    public val typography: SKTypographyTokens
    public val spacing: SKSpacingTokens
    public val elevation: SKElevationTokens
    public val radius: SKRadiusTokens
    public val motion: SKMotionTokens
    public val icons: SKIconTokens
}
