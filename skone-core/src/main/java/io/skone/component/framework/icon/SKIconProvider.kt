package io.skone.component.framework.icon

/**
 * Logical icon key resolved by an [SKIconProvider].
 *
 * Widgets pass keys such as `skone.icon.close`; providers map to Compose ImageVector / XML drawables.
 *
 * @property key Stable icon identifier.
 * @property contentDescription Optional screen-reader description. When null or blank, field
 *   widgets treat the icon as decorative (no separate announcement of the raw [key]). Pass an
 *   explicit non-blank value only when the icon should be announced on its own.
 */
public data class SKIconKey(
    public val key: String,
    public val contentDescription: String? = null,
)

/**
 * Resolved icon reference without UI-framework types.
 *
 * @property key Original key.
 * @property resourceName Optional Android resource name hint for XML bridges.
 * @property vectorName Optional Compose vector catalog name.
 * @property metadata Opaque provider metadata.
 */
public data class SKIconRef(
    public val key: SKIconKey,
    public val resourceName: String? = null,
    public val vectorName: String? = null,
    public val metadata: Map<String, String> = emptyMap(),
)

/**
 * Resolves logical icon keys for components.
 *
 * Apps / theme modules supply implementations; core ships a no-op.
 */
public interface SKIconProvider {
    public fun resolve(key: SKIconKey): SKIconRef?

    public fun resolve(key: String): SKIconRef? = resolve(SKIconKey(key))
}

/**
 * No-op icon provider that always returns `null`.
 */
public object SKNoOpIconProvider : SKIconProvider {
    override fun resolve(key: SKIconKey): SKIconRef? = null
}

/**
 * Map-backed icon provider for tests and simple apps.
 */
public class SKMapIconProvider(
    private val icons: Map<String, SKIconRef>,
) : SKIconProvider {
    override fun resolve(key: SKIconKey): SKIconRef? = icons[key.key]?.copy(key = key)
}
