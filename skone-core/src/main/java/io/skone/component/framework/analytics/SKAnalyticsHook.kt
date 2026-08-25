package io.skone.component.framework.analytics

import io.skone.common.annotation.SKInternal
import io.skone.component.SKAnalyticsConfig
import io.skone.component.framework.event.SKComponentEvent

/**
 * Analytics sink for component framework events.
 *
 * Provider-agnostic — wire to Firebase, Segment, custom loggers in app code.
 */
public interface SKAnalyticsHook {
    /**
     * Records an analytics event.
     *
     * @param name Event name.
     * @param properties Event properties.
     * @param config Optional component analytics config.
     */
    public fun track(
        name: String,
        properties: Map<String, String> = emptyMap(),
        config: SKAnalyticsConfig? = null,
    )

    /**
     * Maps a framework [SKComponentEvent] to an analytics track call when [config] is enabled.
     */
    public fun trackEvent(event: SKComponentEvent, config: SKAnalyticsConfig?) {
        if (config == null || !config.enabled) return
        val props = buildMap {
            putAll(config.properties)
            put("componentId", event.componentId)
            put("componentType", event.componentType)
            put("event", event::class.simpleName.orEmpty())
        }
        track(
            name = "${config.componentName}.${event::class.simpleName}",
            properties = props,
            config = config,
        )
    }
}

/**
 * Discards all analytics events.
 */
public object SKNoOpAnalyticsHook : SKAnalyticsHook {
    override fun track(name: String, properties: Map<String, String>, config: SKAnalyticsConfig?): Unit = Unit
}

/**
 * Collecting hook for tests.
 *
 * **Internal test utility** — not intended for application use.
 */
@SKInternal
public class SKRecordingAnalyticsHook : SKAnalyticsHook {
    public data class Entry(
        public val name: String,
        public val properties: Map<String, String>,
    )

    private val _entries = mutableListOf<Entry>()
    public val entries: List<Entry> get() = _entries.toList()

    override fun track(name: String, properties: Map<String, String>, config: SKAnalyticsConfig?) {
        if (config?.enabled == false) return
        _entries += Entry(name, properties)
    }

    public fun clear() {
        _entries.clear()
    }
}
