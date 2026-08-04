package io.skone.compose.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import io.skone.forms.SKFormController

/**
 * CompositionLocal for the active [SKFormController].
 * Input widgets auto-register when this is non-null.
 */
public val LocalSKFormController: ProvidableCompositionLocal<SKFormController?> =
    staticCompositionLocalOf { null }

public val skFormController: SKFormController?
    @Composable
    @ReadOnlyComposable
    get() = LocalSKFormController.current

@Composable
public fun ProvideSKFormController(
    controller: SKFormController,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSKFormController provides controller, content = content)
}
