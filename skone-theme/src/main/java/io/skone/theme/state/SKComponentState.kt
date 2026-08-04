package io.skone.theme.state

/**
 * High-level interaction state used when resolving tokenized appearances.
 *
 * Widgets map pointer/keyboard/hover into these values; they never pick raw colors.
 */
public enum class SKInteractionState {
    Enabled,
    Disabled,
    Hovered,
    Focused,
    Pressed,
    Dragged,
}

/**
 * Canonical interactive / visual state for SKOne components.
 *
 * This is the shared state model Compose and XML widgets will bind to.
 * Prefer passing [SKComponentState] (or deriving it) instead of scattered booleans
 * at call sites when multiple flags are needed.
 *
 * @property enabled Whether the component accepts interaction.
 * @property readOnly Whether the value is visible but not editable.
 * @property focused Whether the component has input focus.
 * @property pressed Whether the component is actively pressed.
 * @property hovered Whether a pointer is hovering (where applicable).
 * @property selected Whether the component is selected (lists, chips, …).
 * @property checked Tri-state checked for toggles; `null` when not applicable.
 * @property expanded Expansion state for disclosure controls; `null` when N/A.
 * @property loading Whether a busy/loading indicator should be shown.
 * @property error Whether the component is in an error / invalid visual state.
 */
public data class SKComponentState(
    public val enabled: Boolean = true,
    public val readOnly: Boolean = false,
    public val focused: Boolean = false,
    public val pressed: Boolean = false,
    public val hovered: Boolean = false,
    public val selected: Boolean = false,
    public val checked: Boolean? = null,
    public val expanded: Boolean? = null,
    public val loading: Boolean = false,
    public val error: Boolean = false,
) {
    /**
     * Derives the primary [SKInteractionState] for token resolution.
     *
     * Priority: Disabled > Pressed > Focused > Hovered > Enabled.
     */
    public fun interactionState(): SKInteractionState = when {
        !enabled -> SKInteractionState.Disabled
        pressed -> SKInteractionState.Pressed
        focused -> SKInteractionState.Focused
        hovered -> SKInteractionState.Hovered
        else -> SKInteractionState.Enabled
    }

    public companion object {
        /** Default idle, enabled state. */
        public val Default: SKComponentState = SKComponentState()

        /** Disabled convenience. */
        public val Disabled: SKComponentState = SKComponentState(enabled = false)

        /** Error convenience. */
        public val Error: SKComponentState = SKComponentState(error = true)
    }
}
