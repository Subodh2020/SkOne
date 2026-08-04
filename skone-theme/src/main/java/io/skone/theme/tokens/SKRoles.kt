package io.skone.theme.tokens

/**
 * Semantic color role used by appearance configs.
 *
 * Widgets reference roles; the theme supplies concrete [SKColor] values.
 */
public enum class SKColorRole {
    Primary,
    OnPrimary,
    PrimaryContainer,
    OnPrimaryContainer,
    Secondary,
    OnSecondary,
    SecondaryContainer,
    OnSecondaryContainer,
    Tertiary,
    OnTertiary,
    TertiaryContainer,
    OnTertiaryContainer,
    Error,
    OnError,
    ErrorContainer,
    OnErrorContainer,
    Background,
    OnBackground,
    Surface,
    OnSurface,
    SurfaceVariant,
    OnSurfaceVariant,
    Outline,
    OutlineVariant,
    InverseSurface,
    InverseOnSurface,
    InversePrimary,
}

/**
 * Resolves a [SKColorRole] against this token table.
 */
public fun SKColorTokens.color(role: SKColorRole): SKColor = when (role) {
    SKColorRole.Primary -> primary
    SKColorRole.OnPrimary -> onPrimary
    SKColorRole.PrimaryContainer -> primaryContainer
    SKColorRole.OnPrimaryContainer -> onPrimaryContainer
    SKColorRole.Secondary -> secondary
    SKColorRole.OnSecondary -> onSecondary
    SKColorRole.SecondaryContainer -> secondaryContainer
    SKColorRole.OnSecondaryContainer -> onSecondaryContainer
    SKColorRole.Tertiary -> tertiary
    SKColorRole.OnTertiary -> onTertiary
    SKColorRole.TertiaryContainer -> tertiaryContainer
    SKColorRole.OnTertiaryContainer -> onTertiaryContainer
    SKColorRole.Error -> error
    SKColorRole.OnError -> onError
    SKColorRole.ErrorContainer -> errorContainer
    SKColorRole.OnErrorContainer -> onErrorContainer
    SKColorRole.Background -> background
    SKColorRole.OnBackground -> onBackground
    SKColorRole.Surface -> surface
    SKColorRole.OnSurface -> onSurface
    SKColorRole.SurfaceVariant -> surfaceVariant
    SKColorRole.OnSurfaceVariant -> onSurfaceVariant
    SKColorRole.Outline -> outline
    SKColorRole.OutlineVariant -> outlineVariant
    SKColorRole.InverseSurface -> inverseSurface
    SKColorRole.InverseOnSurface -> inverseOnSurface
    SKColorRole.InversePrimary -> inversePrimary
}

/**
 * Typography role for appearance configs.
 */
public enum class SKTypographyRole {
    DisplayLarge,
    DisplayMedium,
    DisplaySmall,
    HeadlineLarge,
    HeadlineMedium,
    HeadlineSmall,
    TitleLarge,
    TitleMedium,
    TitleSmall,
    BodyLarge,
    BodyMedium,
    BodySmall,
    LabelLarge,
    LabelMedium,
    LabelSmall,
}

/**
 * Resolves a [SKTypographyRole] against this token table.
 */
public fun SKTypographyTokens.scale(role: SKTypographyRole): SKTypeScale = when (role) {
    SKTypographyRole.DisplayLarge -> displayLarge
    SKTypographyRole.DisplayMedium -> displayMedium
    SKTypographyRole.DisplaySmall -> displaySmall
    SKTypographyRole.HeadlineLarge -> headlineLarge
    SKTypographyRole.HeadlineMedium -> headlineMedium
    SKTypographyRole.HeadlineSmall -> headlineSmall
    SKTypographyRole.TitleLarge -> titleLarge
    SKTypographyRole.TitleMedium -> titleMedium
    SKTypographyRole.TitleSmall -> titleSmall
    SKTypographyRole.BodyLarge -> bodyLarge
    SKTypographyRole.BodyMedium -> bodyMedium
    SKTypographyRole.BodySmall -> bodySmall
    SKTypographyRole.LabelLarge -> labelLarge
    SKTypographyRole.LabelMedium -> labelMedium
    SKTypographyRole.LabelSmall -> labelSmall
}

/**
 * Elevation level for appearance configs.
 */
public enum class SKElevationLevel {
    Level0,
    Level1,
    Level2,
    Level3,
    Level4,
    Level5,
}

/**
 * Resolves an [SKElevationLevel] against this token table.
 */
public fun SKElevationTokens.dp(level: SKElevationLevel): SKDp = when (level) {
    SKElevationLevel.Level0 -> level0
    SKElevationLevel.Level1 -> level1
    SKElevationLevel.Level2 -> level2
    SKElevationLevel.Level3 -> level3
    SKElevationLevel.Level4 -> level4
    SKElevationLevel.Level5 -> level5
}
