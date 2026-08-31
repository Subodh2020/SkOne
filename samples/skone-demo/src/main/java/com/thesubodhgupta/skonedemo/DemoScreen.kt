package com.thesubodhgupta.skonedemo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.skone.common.result.SKResult
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.component.validation.SKValidationResult
import io.skone.forms.validation.SKEmailRule
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType

private const val SKONE_VERSION = "1.4.0-alpha01"

@Composable
fun DemoScreen(modifier: Modifier = Modifier) {
    val theme = skTheme
    val statusMessage = remember {
        when (val result = SKResult.success("SKOne APIs resolved from Maven Central")) {
            is SKResult.Success -> result.value
            is SKResult.Failure -> result.error.message
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.tokens.colors.background.toColor())
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SKText(
            text = "SKOne Demo",
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = SKTypographyRole.HeadlineMedium,
                contentColorRole = SKColorRole.OnBackground,
            ),
            accessibility = SKAccessibilityConfig(
                contentDescription = "SKOne Demo",
                heading = true,
            ),
        )
        SKText(
            text = "Version: $SKONE_VERSION",
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = SKTypographyRole.BodyLarge,
                contentColorRole = SKColorRole.OnBackground,
            ),
        )
        SKText(
            text = statusMessage,
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = SKTypographyRole.BodyMedium,
                contentColorRole = SKColorRole.Primary,
            ),
        )
        SKText(
            text = "Rendered with SKTheme + SKTextField from com.thesubodhgupta.skone:skone-compose",
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = SKTypographyRole.BodySmall,
                contentColorRole = SKColorRole.OnSurfaceVariant,
            ),
        )

        var email by remember { mutableStateOf("") }
        val emailRule = remember { SKEmailRule() }
        val emailValidation = remember(email) { emailRule.validate(email) }
        val emailSupportingText = when {
            email.isBlank() -> "Enter your email address"
            emailValidation is SKValidationResult.Invalid ->
                emailValidation.errors.firstOrNull()?.message ?: "Enter a valid email address"
            else -> "Looks good"
        }

        SKTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            fieldId = "demo_email",
            label = "Email",
            hint = "Enter your email",
            supportingText = emailSupportingText,
            required = true,
            keyboardType = SKKeyboardType.Email,
            imeAction = SKImeAction.Done,
            accessibility = SKAccessibilityConfig(
                contentDescription = "Email address",
                testTag = "demo_email",
            ),
        )
        SKText(
            text = if (email.isBlank()) {
                "Live value: (empty)"
            } else {
                "Live value: $email"
            },
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = SKTypographyRole.BodyMedium,
                contentColorRole = SKColorRole.Secondary,
            ),
        )
    }
}
