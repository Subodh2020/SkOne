package io.skone.ui.field

/**
 * Visual feedback state for input fields beyond raw [io.skone.theme.state.SKComponentState].
 */
public enum class SKFieldVisualState {
    /** Neutral idle / focused presentation. */
    None,

    /** Validation passed / affirmative feedback. */
    Success,

    /** Validation failed. */
    Error,
}

/**
 * IME action forwarded to the platform keyboard and focus chain.
 */
public enum class SKImeAction {
    Default,
    Done,
    Go,
    Next,
    Previous,
    Search,
    Send,
    None,
}

/**
 * Keyboard type hint for the platform IME.
 */
public enum class SKKeyboardType {
    Text,
    Ascii,
    Number,
    Phone,
    Email,
    Password,
    Uri,
}
