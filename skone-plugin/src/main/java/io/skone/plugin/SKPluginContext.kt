package io.skone.plugin

import io.skone.common.annotation.SKInternal
import io.skone.common.log.SKLogger
import kotlin.reflect.KClass

/**
 * Runtime context provided to plugins during [SKPlugin.onAttach].
 *
 * Dependency lookup is explicit and type-safe — no reflection-based scanning.
 */
public interface SKPluginContext {
    /** Logger installed for the current SKOne instance. */
    public val logger: SKLogger

    /**
     * Returns a previously registered dependency of [type], or `null`.
     */
    public fun <T : Any> getDependency(type: KClass<T>): T?
}

/**
 * Convenience reified lookup for [SKPluginContext.getDependency].
 */
public inline fun <reified T : Any> SKPluginContext.getDependency(): T? =
    getDependency(T::class)

/**
 * Default [SKPluginContext] backed by an in-memory dependency map.
 *
 * **Internal implementation** — not intended for application use.
 */
@SKInternal
public class SKDefaultPluginContext(
    override val logger: SKLogger,
    private val dependencies: Map<KClass<*>, Any> = emptyMap(),
) : SKPluginContext {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getDependency(type: KClass<T>): T? =
        dependencies[type] as? T
}
