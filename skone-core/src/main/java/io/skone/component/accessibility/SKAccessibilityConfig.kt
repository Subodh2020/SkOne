package io.skone.component.accessibility

/**
 * Accessibility contract for SKOne components.
 *
 * Maps to Compose semantics / View accessibility APIs in UI modules.
 *
 * @property contentDescription Primary screen-reader description.
 * @property stateDescription Additional state text (e.g. "selected", "error").
 * @property testTag Test identifier for UI tests.
 * @property role Optional accessibility role hint (`button`, `checkbox`, …).
 * @property heading Whether this node is a heading.
 * @property liveRegion Whether updates should be announced (polite/assertive apps decide).
 * @property traversalIndex Optional focus traversal hint; lower runs earlier.
 * @property mergeDescendants Whether child semantics should merge into this node.
 */
public data class SKAccessibilityConfig(
    public val contentDescription: String? = null,
    public val stateDescription: String? = null,
    public val testTag: String? = null,
    public val role: String? = null,
    public val heading: Boolean = false,
    public val liveRegion: Boolean = false,
    public val traversalIndex: Float? = null,
    public val mergeDescendants: Boolean = false,
) {
    public companion object {
        /** Empty accessibility config. */
        public val None: SKAccessibilityConfig = SKAccessibilityConfig()
    }
}
