package io.skone

import io.skone.ai.SKAIConfig
import io.skone.ai.SKAIProvider
import io.skone.ai.SKAIRequest
import io.skone.ai.SKAIResponse
import io.skone.common.error.SKError
import io.skone.common.log.SKLog
import io.skone.common.log.SKLogger
import io.skone.common.result.SKResult
import io.skone.plugin.SKDefaultPluginContext
import io.skone.plugin.SKInMemoryPluginRegistry
import io.skone.plugin.SKPluginRegistry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

/**
 * Entry point for the SKOne SDK.
 *
 * ### Usage
 * ```kotlin
 * class SampleApp : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         SKOne.initialize(
 *             SKOneConfig(plugins = listOf(DemoPlugin()))
 *         )
 *     }
 * }
 * ```
 *
 * **Double-init policy:** calling [initialize] again replaces the previous
 * configuration, warns via the logger, and re-registers plugins
 * (idempotent replace with warning). See ADR 0002 / plan foundation notes.
 */
public object SKOne {
    private const val TAG = "SKOne"

    private val initialized = AtomicBoolean(false)
    private val loggerRef = AtomicReference<SKLogger?>(null)
    private val registryRef = AtomicReference<SKPluginRegistry?>(null)
    private val aiConfigRef = AtomicReference(SKAIConfig.Disabled)
    private val providers = ConcurrentHashMap<String, SKAIProvider>()

    /**
     * Initializes SKOne. Safe to call multiple times; subsequent calls replace
     * the previous configuration with a warning.
     */
    @JvmStatic
    public fun initialize(config: SKOneConfig = SKOneConfig()) {
        if (initialized.getAndSet(true)) {
            config.logger.w(TAG, "SKOne.initialize called again; replacing previous configuration")
            // Detach existing plugins before re-binding
            registryRef.get()?.all()?.forEach { plugin ->
                runCatching { registryRef.get()?.unregister(plugin.id) }
            }
        }

        SKLog.install(config.logger)
        loggerRef.set(config.logger)

        val dependencies = ConcurrentHashMap<KClass<*>, Any>()
        val registry = SKInMemoryPluginRegistry {
            SKDefaultPluginContext(
                logger = config.logger,
                dependencies = dependencies.toMap(),
            )
        }
        registryRef.set(registry)

        providers.clear()
        config.ai.providers.forEach { provider ->
            providers[provider.id] = provider
        }
        aiConfigRef.set(config.ai)

        config.plugins.forEach { registry.register(it) }

        config.logger.i(TAG, "SKOne initialized (plugins=${config.plugins.size}, aiProviders=${config.ai.providers.size})")
    }

    /** `true` after the first successful [initialize] call. */
    @JvmStatic
    public fun isInitialized(): Boolean = initialized.get()

    /**
     * Returns the plugin registry.
     *
     * @throws IllegalStateException if [initialize] has not been called.
     */
    @JvmStatic
    public fun plugins(): SKPluginRegistry =
        registryRef.get() ?: error("SKOne is not initialized. Call SKOne.initialize() first.")

    /**
     * Returns the installed logger.
     *
     * @throws IllegalStateException if [initialize] has not been called.
     */
    @JvmStatic
    public fun logger(): SKLogger =
        loggerRef.get() ?: error("SKOne is not initialized. Call SKOne.initialize() first.")

    /**
     * Returns the active AI configuration.
     */
    @JvmStatic
    public fun aiConfig(): SKAIConfig = aiConfigRef.get()

    /**
     * Resolves an AI provider by id, or the default from [SKAIConfig].
     */
    @JvmStatic
    public fun aiProvider(providerId: String? = null): SKAIProvider? {
        val config = aiConfigRef.get()
        val id = providerId ?: config.defaultProviderId
        return if (id != null) providers[id] else providers.values.firstOrNull()
    }

    /**
     * Runs an AI completion using the default or specified provider.
     *
     * Returns [SKResult.Failure] with [SKError.CODE_AI_UNAVAILABLE] when AI is
     * disabled or no provider is registered.
     */
    @JvmStatic
    public suspend fun aiComplete(
        request: SKAIRequest,
        providerId: String? = null,
    ): SKResult<SKAIResponse> {
        val provider = aiProvider(providerId)
            ?: return SKResult.failure(
                code = SKError.CODE_AI_UNAVAILABLE,
                message = "No AI provider is available. Register providers via SKAIConfig.",
            )
        return provider.complete(request)
    }

    /**
     * Resets SDK state. Intended for unit tests only.
     */
    @JvmStatic
    public fun resetForTest() {
        registryRef.get()?.all()?.forEach { plugin ->
            runCatching { registryRef.get()?.unregister(plugin.id) }
        }
        initialized.set(false)
        loggerRef.set(null)
        registryRef.set(null)
        aiConfigRef.set(SKAIConfig.Disabled)
        providers.clear()
        SKLog.resetForTest()
    }
}
