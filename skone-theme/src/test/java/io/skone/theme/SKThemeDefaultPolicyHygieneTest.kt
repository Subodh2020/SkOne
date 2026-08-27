package io.skone.theme

import io.skone.common.annotation.SKInternal
import io.skone.theme.defaults.SKDarkColorTokens
import io.skone.theme.defaults.SKLightColorTokens
import io.skone.theme.size.SKSize
import io.skone.theme.tokens.SKColor
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKColorTokens
import io.skone.theme.tokens.color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Theme default policy hygiene for 1.4:
 * - Flagship [SKThemes] / [SKThemeBuilder] / custom [SKThemeProvider] stay OptIn-free.
 * - Public color seeds remain usable.
 * - Internal default packs require `@OptIn(SKInternal)`.
 */
class SKThemeDefaultPolicyHygieneTest {

    @Test
    fun skThemesLightAndDarkRemainDistinctWithoutOptIn() {
        assertEquals("skone.light", SKThemes.Light.name)
        assertEquals("skone.dark", SKThemes.Dark.name)
        assertEquals(SKThemeMode.Light, SKThemes.Light.mode)
        assertEquals(SKThemeMode.Dark, SKThemes.Dark.mode)
        assertNotEquals(SKThemes.Light.tokens.colors.primary, SKThemes.Dark.tokens.colors.primary)
        assertTrue(SKThemes.Light.sizes.minTouchTarget.value >= 48f)
        assertNotNull(SKThemes.Light.shapes.medium)
    }

    @Test
    fun skThemeConstructorDefaultsResolveWithoutNamingImplTypes() {
        val theme = SKTheme(
            name = "probe",
            mode = SKThemeMode.Light,
            tokens = SKThemes.Light.tokens,
        )
        assertEquals(SKThemes.Light.sizes.height(SKSize.Medium), theme.sizes.height(SKSize.Medium))
        assertEquals(SKThemes.Light.shapes.medium, theme.shapes.medium)
    }

    @Test
    fun themeBuilderStillWorksWithPublicColorSeeds() {
        val custom = SKLightColorTokens(primary = SKColor(0xFF112233.toInt()))
        val theme = SKThemeBuilder.light()
            .name("seeded")
            .colors(custom)
            .build()
        assertEquals("seeded", theme.name)
        assertEquals(custom.primary, theme.tokens.colors.primary)
        assertEquals(SKThemes.Light.tokens.spacing.md, theme.tokens.spacing.md)
    }

    @Test
    fun darkColorSeedRemainsPubliclyUsable() {
        val dark = SKDarkColorTokens(primary = SKColor(0xFFAABBCC.toInt()))
        val theme = SKThemeBuilder.dark().colors(dark).build()
        assertEquals(dark.primary, theme.tokens.colors.primary)
    }

    @Test
    fun customThemeProviderWorksWithoutOptIn() {
        val custom = object : SKThemeProvider {
            override fun theme(mode: SKThemeMode, isSystemInDarkTheme: Boolean): SKTheme =
                SKThemes.Light.copy(name = "custom.provider")
        }
        assertEquals("custom.provider", custom.theme(SKThemeMode.System, false).name)
    }

    @Test
    fun colorRoleResolutionUnchanged() {
        assertEquals(
            SKThemes.Light.tokens.colors.primary,
            SKThemes.Light.tokens.colors.color(SKColorRole.Primary),
        )
    }

    @Test
    @OptIn(SKInternal::class)
    fun internalizedDefaultProviderStillCallableWithOptIn() {
        val provider = SKDefaultThemeProvider()
        assertEquals(SKThemes.Light.name, provider.theme(SKThemeMode.Light).name)
        assertEquals(SKThemes.Dark.name, provider.theme(SKThemeMode.Dark).name)
    }

    @Test
    fun skThemeAcceptsCustomPublicTokenInterfaces() {
        val colors = object : SKColorTokens by SKThemes.Light.tokens.colors {
            override val primary: SKColor = SKColor(0xFF010203.toInt())
        }
        val theme = SKThemeBuilder.from(SKThemes.Light).colors(colors).build()
        assertEquals(SKColor(0xFF010203.toInt()), theme.tokens.colors.primary)
    }
}
