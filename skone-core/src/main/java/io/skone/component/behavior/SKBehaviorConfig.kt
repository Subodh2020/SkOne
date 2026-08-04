package io.skone.component.behavior

/**
 * Interaction behavior contract for future SKOne widgets.
 *
 * Keeps behavior orthogonal to appearance and validation.
 *
 * @property enabled When false, mirrors disabled component state (may also be set via state).
 * @property clickable Whether click / tap gestures are handled.
 * @property focusable Whether the component can take focus.
 * @property longPressEnabled Whether long-press is handled.
 * @property hapticFeedback Whether haptics may fire on primary interactions.
 * @property debounceMillis Optional tap debounce window; `null` means no debounce.
 * @property repeatable Whether holding may emit repeated actions (steppers, etc.).
 */
public data class SKBehaviorConfig(
    public val enabled: Boolean = true,
    public val clickable: Boolean = true,
    public val focusable: Boolean = true,
    public val longPressEnabled: Boolean = false,
    public val hapticFeedback: Boolean = false,
    public val debounceMillis: Long? = null,
    public val repeatable: Boolean = false,
) {
    public companion object {
        /** Default interactive behavior. */
        public val Default: SKBehaviorConfig = SKBehaviorConfig()

        /** Non-interactive display-only behavior. */
        public val Passive: SKBehaviorConfig = SKBehaviorConfig(
            enabled = true,
            clickable = false,
            focusable = false,
        )
    }
}
