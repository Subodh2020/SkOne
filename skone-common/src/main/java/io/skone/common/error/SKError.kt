package io.skone.common.error

/**
 * Structured error used across SKOne public APIs.
 *
 * [code] values are part of the public contract and should remain stable.
 *
 * @property code Machine-readable error code (stable).
 * @property message Human-readable description.
 * @property cause Optional underlying cause.
 * @property metadata Optional diagnostic key/value pairs.
 *
 * @see docs/adr/0004-result-and-error-model.md
 */
public data class SKError(
    public val code: String,
    public val message: String,
    public val cause: Throwable? = null,
    public val metadata: Map<String, String> = emptyMap(),
) {
    public companion object {
        /** Generic unknown failure. */
        public const val CODE_UNKNOWN: String = "skone.unknown"

        /** SDK used before [io.skone.SKOne.initialize]. */
        public const val CODE_NOT_INITIALIZED: String = "skone.not_initialized"

        /** Invalid configuration supplied by the consumer. */
        public const val CODE_INVALID_CONFIG: String = "skone.invalid_config"

        /** Requested plugin or dependency was not found. */
        public const val CODE_NOT_FOUND: String = "skone.not_found"

        /** AI provider is missing or disabled. */
        public const val CODE_AI_UNAVAILABLE: String = "skone.ai.unavailable"

        /**
         * Creates an [SKError] with [CODE_UNKNOWN].
         */
        public fun unknown(
            message: String,
            cause: Throwable? = null,
            metadata: Map<String, String> = emptyMap(),
        ): SKError = SKError(CODE_UNKNOWN, message, cause, metadata)
    }
}
