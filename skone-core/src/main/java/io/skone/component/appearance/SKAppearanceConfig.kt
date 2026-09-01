package io.skone.component.appearance

import io.skone.theme.shape.SKShape
import io.skone.theme.shape.SKShapeStyle
import io.skone.theme.size.SKSize
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKElevationLevel
import io.skone.theme.tokens.SKTypographyRole

/**
 * Appearance contract for future SKOne widgets.
 *
 * All visual choices are **roles / semantic sizes** resolved against [io.skone.theme.SKTheme].
 * Do not put raw ARGB, dp, or sp literals here.
 *
 * @property size Semantic control size.
 * @property shapeStyle Named shape from theme shape tokens; ignored when [shape] is set.
 * @property shape Optional explicit shape override (still token-backed via [io.skone.theme.shape.SKShape.Rounded]).
 * @property containerColorRole Fill / container color role.
 * @property contentColorRole Foreground / content color role.
 * @property outlineColorRole Optional outline / border color role.
 * @property typographyRole Optional text role for labeled components.
 * @property elevation Optional elevation level.
 */
public data class SKAppearanceConfig(
    public val size: SKSize = SKSize.Medium,
    public val shapeStyle: SKShapeStyle = SKShapeStyle.Medium,
    public val shape: SKShape? = null,
    public val containerColorRole: SKColorRole = SKColorRole.Primary,
    public val contentColorRole: SKColorRole = SKColorRole.OnPrimary,
    public val outlineColorRole: SKColorRole? = null,
    public val typographyRole: SKTypographyRole? = SKTypographyRole.LabelLarge,
    public val elevation: SKElevationLevel? = null,
) {
    public companion object {
        /** Neutral surface appearance (cards, containers). */
        public val Surface: SKAppearanceConfig = SKAppearanceConfig(
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.OutlineVariant,
            typographyRole = SKTypographyRole.BodyMedium,
            elevation = SKElevationLevel.Level0,
        )

        /** Primary emphasis appearance (primary actions). */
        public val Primary: SKAppearanceConfig = SKAppearanceConfig()

        /** Error appearance. */
        public val Error: SKAppearanceConfig = SKAppearanceConfig(
            containerColorRole = SKColorRole.Error,
            contentColorRole = SKColorRole.OnError,
        )

        /** Default body text on surface (for SKText and similar). */
        public val Text: SKAppearanceConfig = SKAppearanceConfig(
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            typographyRole = SKTypographyRole.BodyLarge,
            outlineColorRole = null,
            elevation = null,
        )

        /** Default text field: surface container, on-surface content, outline. */
        public val TextField: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.Medium,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.Outline,
            typographyRole = SKTypographyRole.BodyLarge,
            elevation = SKElevationLevel.Level0,
        )

        /** Success-emphasis field chrome (outline/supporting use tertiary/primary as success proxy). */
        public val TextFieldSuccess: SKAppearanceConfig = TextField.copy(
            outlineColorRole = SKColorRole.Primary,
        )

        /** Error-emphasis field chrome. */
        public val TextFieldError: SKAppearanceConfig = TextField.copy(
            outlineColorRole = SKColorRole.Error,
            contentColorRole = SKColorRole.OnSurface,
        )

        /** Filled button (primary container). Alias of [Primary] for widget clarity. */
        public val Button: SKAppearanceConfig = Primary.copy(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Medium,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = SKElevationLevel.Level1,
        )

        /** Tonal / soft-filled button. */
        public val ButtonTonal: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Medium,
            containerColorRole = SKColorRole.PrimaryContainer,
            contentColorRole = SKColorRole.OnPrimaryContainer,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = SKElevationLevel.Level0,
        )

        /** Outlined button. */
        public val ButtonOutlined: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Medium,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.Primary,
            outlineColorRole = SKColorRole.Outline,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = SKElevationLevel.Level0,
        )

        /** Text / borderless button. */
        public val ButtonText: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Medium,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.Primary,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = null,
        )

        /** Checkbox / switch control chrome (primary when active). */
        public val Toggle: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Small,
            containerColorRole = SKColorRole.Primary,
            contentColorRole = SKColorRole.OnPrimary,
            outlineColorRole = SKColorRole.Outline,
            typographyRole = SKTypographyRole.BodyLarge,
            elevation = null,
        )

        /** Icon-only button (compact, no fill emphasis). */
        public val IconButton: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = null,
        )

        /** Chip — unselected / resting. */
        public val Chip: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Small,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.Outline,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = null,
        )

        /** Chip — selected emphasis. */
        public val ChipSelected: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Small,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.SecondaryContainer,
            contentColorRole = SKColorRole.OnSecondaryContainer,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = null,
        )

        /** Hairline divider / separator. */
        public val Divider: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Small,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.OutlineVariant,
            contentColorRole = SKColorRole.OutlineVariant,
            outlineColorRole = null,
            typographyRole = null,
            elevation = null,
        )

        /** Card / elevated surface container. */
        public val Card: SKAppearanceConfig = Surface.copy(
            shapeStyle = SKShapeStyle.Medium,
            outlineColorRole = null,
            elevation = SKElevationLevel.Level1,
            typographyRole = SKTypographyRole.BodyMedium,
        )

        /** Progress track / indicator emphasis. */
        public val Progress: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Small,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.Primary,
            contentColorRole = SKColorRole.Primary,
            outlineColorRole = SKColorRole.SurfaceVariant,
            typographyRole = null,
            elevation = null,
        )

        /** Snackbar inverse surface. */
        public val Snackbar: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Small,
            containerColorRole = SKColorRole.InverseSurface,
            contentColorRole = SKColorRole.InverseOnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.BodyMedium,
            elevation = SKElevationLevel.Level2,
        )

        /** Dialog / alert surface. */
        public val Dialog: SKAppearanceConfig = Surface.copy(
            shapeStyle = SKShapeStyle.Large,
            outlineColorRole = null,
            elevation = SKElevationLevel.Level3,
            typographyRole = SKTypographyRole.BodyMedium,
        )

        /** Top app bar surface. */
        public val TopAppBar: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.TitleLarge,
            elevation = SKElevationLevel.Level0,
        )

        /** Navigation bar surface. */
        public val NavigationBar: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurfaceVariant,
            outlineColorRole = SKColorRole.OutlineVariant,
            typographyRole = SKTypographyRole.LabelMedium,
            elevation = SKElevationLevel.Level2,
        )

        /** List row surface. */
        public val ListItem: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.BodyLarge,
            elevation = null,
        )

        /** Section header typography emphasis. */
        public val SectionHeader: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurfaceVariant,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.TitleSmall,
            elevation = null,
        )

        /** Scaffold / screen background. */
        public val Scaffold: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Background,
            contentColorRole = SKColorRole.OnBackground,
            outlineColorRole = null,
            typographyRole = null,
            elevation = null,
        )

        /** Search bar field chrome. */
        public val SearchBar: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.SurfaceVariant,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.BodyLarge,
            elevation = null,
        )

        /** Empty-state content block. */
        public val EmptyState: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Background,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.TitleMedium,
            elevation = null,
        )

        /** Floating action button. */
        public val Fab: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.PrimaryContainer,
            contentColorRole = SKColorRole.OnPrimaryContainer,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = SKElevationLevel.Level3,
        )

        /** Tab row surface. */
        public val TabRow: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurfaceVariant,
            outlineColorRole = SKColorRole.OutlineVariant,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = null,
        )

        /** Compact badge / count chip. */
        public val Badge: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.ExtraSmall,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.Error,
            contentColorRole = SKColorRole.OnError,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.LabelSmall,
            elevation = null,
        )

        /** Avatar circle. */
        public val Avatar: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.PrimaryContainer,
            contentColorRole = SKColorRole.OnPrimaryContainer,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.TitleMedium,
            elevation = null,
        )

        /** Menu surface. */
        public val Menu: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Medium,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.OutlineVariant,
            typographyRole = SKTypographyRole.BodyLarge,
            elevation = SKElevationLevel.Level2,
        )

        /** Dropdown menu popup surface. */
        public val DropdownMenu: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Medium,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.OutlineVariant,
            typographyRole = SKTypographyRole.BodyLarge,
            elevation = SKElevationLevel.Level3,
        )

        /** Tooltip surface. */
        public val Tooltip: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Small,
            shapeStyle = SKShapeStyle.Small,
            containerColorRole = SKColorRole.InverseSurface,
            contentColorRole = SKColorRole.InverseOnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.BodySmall,
            elevation = SKElevationLevel.Level2,
        )

        /** Bottom app bar chrome. */
        public val BottomAppBar: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.None,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.OutlineVariant,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = SKElevationLevel.Level2,
        )

        /** Bottom sheet surface. */
        public val BottomSheet: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Large,
            shapeStyle = SKShapeStyle.Large,
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = null,
            typographyRole = SKTypographyRole.TitleMedium,
            elevation = SKElevationLevel.Level3,
        )

        /** Segmented button group. */
        public val SegmentedButton: SKAppearanceConfig = SKAppearanceConfig(
            size = SKSize.Medium,
            shapeStyle = SKShapeStyle.Full,
            containerColorRole = SKColorRole.SurfaceVariant,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.Outline,
            typographyRole = SKTypographyRole.LabelLarge,
            elevation = null,
        )
    }
}
