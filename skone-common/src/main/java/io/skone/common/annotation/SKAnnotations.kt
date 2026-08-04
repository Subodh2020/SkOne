package io.skone.common.annotation

/**
 * Marks an SKOne API as experimental.
 *
 * Call sites must opt in with `@OptIn(SKExperimental::class)`.
 * Experimental APIs may change without a major version bump.
 */
@RequiresOptIn(
    message = "This SKOne API is experimental and may change without notice.",
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.CONSTRUCTOR,
)
public annotation class SKExperimental

/**
 * Marks an API as internal to SKOne.
 *
 * Not intended for application use. May change or be removed at any time.
 */
@RequiresOptIn(
    message = "This SKOne API is internal and must not be used by applications.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.CONSTRUCTOR,
)
public annotation class SKInternal
