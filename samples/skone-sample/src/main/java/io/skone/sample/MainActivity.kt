package io.skone.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.skone.SKOne
import io.skone.compose.theme.SKTheme
import io.skone.compose.theme.resolve
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toSp
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.theme.SKThemeMode
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SKTheme(mode = SKThemeMode.System) {
                DesignSystemShowcase(
                    initialized = SKOne.isInitialized(),
                    pluginCount = if (SKOne.isInitialized()) SKOne.plugins().all().size else 0,
                )
            }
        }
    }
}

@Composable
fun DesignSystemShowcase(
    initialized: Boolean,
    pluginCount: Int,
    modifier: Modifier = Modifier,
) {
    val theme = skTheme
    val colors = theme.tokens.colors
    val body = theme.tokens.typography.scale(SKTypographyRole.BodyLarge)
    val title = theme.tokens.typography.scale(SKTypographyRole.HeadlineSmall)
    val primarySwatch = SKAppearanceConfig.Primary.resolve()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background.toColor())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "SKOne Design System",
            color = colors.onBackground.toColor(),
            fontSize = title.size.toSp(),
            lineHeight = title.lineHeight.toSp(),
        )
        Text(
            text = if (initialized) {
                "SDK initialized · $pluginCount plugin(s) · theme=${theme.name}"
            } else {
                "SDK not initialized"
            },
            color = colors.onSurfaceVariant.toColor(),
            fontSize = body.size.toSp(),
            lineHeight = body.lineHeight.toSp(),
        )

        Text(
            text = "Token swatches (not widgets)",
            color = colors.onBackground.toColor(),
            fontSize = theme.tokens.typography.titleMedium.size.toSp(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TokenSwatch(label = "Primary", color = colors.primary.toColor())
            TokenSwatch(label = "Secondary", color = colors.secondary.toColor())
            TokenSwatch(label = "Error", color = colors.error.toColor())
            TokenSwatch(label = "Surface", color = colors.surfaceVariant.toColor())
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(primarySwatch.height)
                .clip(primarySwatch.shape)
                .background(primarySwatch.containerColor)
                .padding(
                    horizontal = primarySwatch.horizontalPadding,
                    vertical = primarySwatch.verticalPadding,
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "Resolved SKAppearanceConfig.Primary",
                color = primarySwatch.contentColor,
            )
        }

        Text(
            text = "Spacing md=${theme.tokens.spacing.md.value}dp · " +
                "Radius md=${theme.tokens.radius.md.value}dp · " +
                "Min touch=${theme.sizes.minTouchTarget.value}dp",
            color = colors.onSurfaceVariant.toColor(),
            fontSize = theme.tokens.typography.bodySmall.size.toSp(),
        )
    }
}

@Composable
private fun TokenSwatch(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = skTheme.tokens.colors.onBackground.toColor(),
            fontSize = skTheme.tokens.typography.labelSmall.size.toSp(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DesignSystemShowcasePreview() {
    SKTheme(mode = SKThemeMode.Light) {
        DesignSystemShowcase(initialized = true, pluginCount = 1)
    }
}
