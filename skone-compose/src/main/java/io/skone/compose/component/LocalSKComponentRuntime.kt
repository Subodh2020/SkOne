package io.skone.compose.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import io.skone.component.framework.SKComponentRuntime

/**
 * Optional composition-local [SKComponentRuntime] for SK widgets.
 */
public val LocalSKComponentRuntime: ProvidableCompositionLocal<SKComponentRuntime?> =
    staticCompositionLocalOf { null }

/**
 * Current [SKComponentRuntime], or `null` if not provided.
 */
public val skComponentRuntime: SKComponentRuntime?
    @Composable
    @ReadOnlyComposable
    get() = LocalSKComponentRuntime.current

/**
 * Provides [runtime] to descendant SK widgets.
 */
@Composable
public fun ProvideSKComponentRuntime(
    runtime: SKComponentRuntime,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSKComponentRuntime provides runtime, content = content)
}
