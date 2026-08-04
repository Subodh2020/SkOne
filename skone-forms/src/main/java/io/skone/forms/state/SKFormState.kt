package io.skone.forms.state

/**
 * Lifecycle phase of an [io.skone.forms.SKFormController].
 */
public enum class SKFormLifecycle {
    /** Controller created; no fields registered yet or pristine. */
    Created,

    /** Fields registered; user may edit. */
    Ready,

    /** At least one field value changed since last reset/submit. */
    Dirty,

    /** Validation in progress. */
    Validating,

    /** Submit in progress (after successful validation). */
    Submitting,

    /** Last submit completed successfully. */
    Submitted,

    /** Form-level or field-level errors present after validate/submit. */
    Invalid,

    /** Controller disposed; no further mutations. */
    Disposed,
}

/**
 * Immutable snapshot of form runtime state.
 *
 * @property lifecycle Current lifecycle phase.
 * @property isDirty `true` when any field differs from its initial value.
 * @property isValid `true` when the last validation produced no errors (`null` if never validated).
 * @property submitting `true` while submit is running.
 * @property fieldCount Registered field count.
 * @property errorCount Number of fields with errors.
 */
public data class SKFormState(
    public val lifecycle: SKFormLifecycle = SKFormLifecycle.Created,
    public val isDirty: Boolean = false,
    public val isValid: Boolean? = null,
    public val submitting: Boolean = false,
    public val fieldCount: Int = 0,
    public val errorCount: Int = 0,
) {
    public companion object {
        public val Initial: SKFormState = SKFormState()
    }
}
