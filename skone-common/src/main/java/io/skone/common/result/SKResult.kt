package io.skone.common.result

import io.skone.common.error.SKError

/**
 * A discriminated result type for fallible SKOne APIs.
 *
 * Prefer returning [SKResult] from public APIs instead of throwing for expected failures.
 *
 * ### Usage
 * ```kotlin
 * when (val result = repository.load()) {
 *     is SKResult.Success -> render(result.value)
 *     is SKResult.Failure -> showError(result.error)
 * }
 * ```
 *
 * @see SKError
 * @see docs/adr/0004-result-and-error-model.md
 */
public sealed interface SKResult<out T> {

    /**
     * Successful outcome carrying [value].
     */
    public data class Success<T>(public val value: T) : SKResult<T>

    /**
     * Failed outcome carrying a structured [error].
     */
    public data class Failure(public val error: SKError) : SKResult<Nothing>

    public companion object {
        /**
         * Creates a [Success] result.
         */
        public fun <T> success(value: T): SKResult<T> = Success(value)

        /**
         * Creates a [Failure] result from an [SKError].
         */
        public fun <T> failure(error: SKError): SKResult<T> = Failure(error)

        /**
         * Creates a [Failure] from code and message.
         */
        public fun <T> failure(
            code: String,
            message: String,
            cause: Throwable? = null,
            metadata: Map<String, String> = emptyMap(),
        ): SKResult<T> = Failure(SKError(code, message, cause, metadata))
    }
}

/**
 * Returns the success value or `null` when this is a [SKResult.Failure].
 */
public fun <T> SKResult<T>.getOrNull(): T? = when (this) {
    is SKResult.Success -> value
    is SKResult.Failure -> null
}

/**
 * Returns the [SKError] or `null` when this is a [SKResult.Success].
 */
public fun <T> SKResult<T>.errorOrNull(): SKError? = when (this) {
    is SKResult.Success -> null
    is SKResult.Failure -> error
}

/**
 * Returns the success value or [default] when failed.
 */
public fun <T> SKResult<T>.getOrDefault(default: T): T = getOrNull() ?: default

/**
 * Maps a successful value; failures are preserved unchanged.
 */
public inline fun <T, R> SKResult<T>.map(transform: (T) -> R): SKResult<R> = when (this) {
    is SKResult.Success -> SKResult.Success(transform(value))
    is SKResult.Failure -> this
}

/**
 * Flat-maps a successful value; failures are preserved unchanged.
 */
public inline fun <T, R> SKResult<T>.flatMap(transform: (T) -> SKResult<R>): SKResult<R> = when (this) {
    is SKResult.Success -> transform(value)
    is SKResult.Failure -> this
}

/**
 * Executes [block] when this is [SKResult.Success].
 */
public inline fun <T> SKResult<T>.onSuccess(block: (T) -> Unit): SKResult<T> {
    if (this is SKResult.Success) block(value)
    return this
}

/**
 * Executes [block] when this is [SKResult.Failure].
 */
public inline fun <T> SKResult<T>.onFailure(block: (SKError) -> Unit): SKResult<T> {
    if (this is SKResult.Failure) block(error)
    return this
}
