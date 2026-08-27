@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.skone.common.annotation.SKInternal
import io.skone.common.log.SKLogger
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.analytics.SKAnalyticsHook
import io.skone.component.framework.analytics.SKNoOpAnalyticsHook
import io.skone.component.framework.icon.SKIconProvider
import io.skone.component.framework.icon.SKNoOpIconProvider
import io.skone.component.framework.plugin.SKComponentPlugin

/**
 * Remembers a [SKComponentRuntime] for the composition hierarchy.
 *
 * This is **not** a widget — future SK composables will obtain the runtime via this helper
 * or an explicit parameter.
 *
 * @param logger Optional logger; when null, the library default logger is used.
 */
@Composable
public fun rememberSKComponentRuntime(
    logger: SKLogger? = null,
    icons: SKIconProvider = SKNoOpIconProvider,
    analytics: SKAnalyticsHook = SKNoOpAnalyticsHook,
    plugins: List<SKComponentPlugin> = emptyList(),
): SKComponentRuntime = remember(logger, icons, analytics, plugins) {
    SKComponentRuntime.create(
        logger = logger,
        icons = icons,
        analytics = analytics,
        plugins = plugins,
    )
}

/**
 * Attaches [component] to [runtime] for the composition lifetime.
 *
 * No UI is rendered — this only manages framework lifecycle.
 *
 * Framework plumbing for SK widgets; not intended for application use.
 */
@SKInternal
@Composable
public fun SKComponentLifecycle(
    component: SKComponent,
    runtime: SKComponentRuntime,
) {
    DisposableEffect(component, runtime) {
        component.attach(runtime)
        onDispose { component.detach() }
    }
}

/**
 * Marker / host contract for Compose-backed SKOne components.
 *
 * Future widgets implement this to expose their framework [component] instance.
 * Contains **no** Composable UI surface of its own.
 */
public interface SKComposeComponent {
    /** Framework component backed by this Compose host. */
    public val component: SKComponent
}

/**
 * Remembers attachment of a factory-created [SKComponent] for the composition.
 *
 * Framework plumbing for future SK widgets; not intended for application use.
 *
 * ### Example (library widget author)
 * ```kotlin
 * val button = rememberSKComponent(runtime) { SKButtonComponent(id = "submit") }
 * // then render UI using button.config / button.performClick()
 * ```
 */
@SKInternal
@Composable
public fun <T : SKComponent> rememberSKComponent(
    runtime: SKComponentRuntime,
    key: Any? = null,
    factory: () -> T,
): T {
    val component = remember(key) { factory() }
    SKComponentLifecycle(component = component, runtime = runtime)
    return component
}
