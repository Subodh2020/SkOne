package io.skone.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.component.ProvideSKComponentRuntime
import io.skone.compose.component.rememberSKComponentRuntime
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.SKTheme
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.forms.SKFormController
import io.skone.forms.formatter.SKTrimFormatter
import io.skone.forms.mask.SKInputMasks
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKRequiredRule
import io.skone.theme.SKThemeMode
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType

/**
 * Minimal integration sample for SKTextField + SKFormController.
 *
 * The official developer showcase is [samples:skone-playground].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SKTheme(mode = SKThemeMode.System) {
                val runtime = rememberSKComponentRuntime()
                ProvideSKComponentRuntime(runtime) {
                    TextFieldFormShowcase()
                }
            }
        }
    }
}

@Composable
fun TextFieldFormShowcase(modifier: Modifier = Modifier) {
    val controller = remember { SKFormController.create() }
    DisposableEffect(controller) {
        onDispose { controller.dispose() }
    }

    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val formState by controller.state.collectAsState()
    val errors by controller.errors.errors.collectAsState()
    val theme = skTheme
    val spacing = theme.tokens.spacing

    ProvideSKFormController(controller) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(theme.tokens.colors.background.toColor())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.md.toDp(), vertical = spacing.lg.toDp()),
            verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
        ) {
            SKText(
                text = "SKTextField + Form",
                appearance = SKAppearanceConfig.Text.copy(
                    typographyRole = SKTypographyRole.HeadlineSmall,
                    contentColorRole = SKColorRole.OnBackground,
                ),
                accessibility = SKAccessibilityConfig(
                    contentDescription = "SKTextField form showcase",
                    heading = true,
                ),
            )
            SKText(
                text = "lifecycle=${formState.lifecycle} · dirty=${formState.isDirty} · " +
                    "valid=${formState.isValid} · errors=${formState.errorCount}",
                appearance = SKAppearanceConfig.Text.copy(typographyRole = SKTypographyRole.BodySmall),
            )

            SKTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                fieldId = "email",
                label = "Email",
                hint = "name@company.com",
                required = true,
                rules = listOf(SKRequiredRule(), SKEmailRule()),
                formatter = SKTrimFormatter,
                keyboardType = SKKeyboardType.Email,
                imeAction = SKImeAction.Next,
                accessibility = SKAccessibilityConfig(
                    contentDescription = "Email",
                    testTag = "field_email",
                ),
            )

            SKTextField(
                modifier = Modifier.fillMaxWidth(),
                value = phone,
                onValueChange = { phone = it },
                fieldId = "phone",
                label = "Phone",
                hint = "(555) 123-4567",
                required = true,
                rules = listOf(SKRequiredRule()),
                mask = SKInputMasks.UsPhone,
                keyboardType = SKKeyboardType.Phone,
                imeAction = SKImeAction.Done,
                accessibility = SKAccessibilityConfig(
                    contentDescription = "Phone",
                    testTag = "field_phone",
                ),
            )

            SKText(
                text = "Validate",
                appearance = SKAppearanceConfig.Text.copy(
                    typographyRole = SKTypographyRole.LabelLarge,
                    contentColorRole = SKColorRole.Primary,
                ),
                onClick = { controller.validate() },
            )
            SKText(
                text = "Submit",
                appearance = SKAppearanceConfig.Text.copy(
                    typographyRole = SKTypographyRole.LabelLarge,
                    contentColorRole = SKColorRole.Primary,
                ),
                onClick = { controller.submit() },
            )
            SKText(
                text = "Reset",
                appearance = SKAppearanceConfig.Text.copy(
                    typographyRole = SKTypographyRole.LabelLarge,
                    contentColorRole = SKColorRole.Secondary,
                ),
                onClick = {
                    controller.reset()
                    email = ""
                    phone = ""
                },
            )

            if (errors.isNotEmpty()) {
                SKText(
                    text = "errors=$errors",
                    appearance = SKAppearanceConfig.Text.copy(
                        typographyRole = SKTypographyRole.BodySmall,
                        contentColorRole = SKColorRole.Error,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldFormShowcasePreview() {
    SKTheme(mode = SKThemeMode.Light) {
        TextFieldFormShowcase()
    }
}
