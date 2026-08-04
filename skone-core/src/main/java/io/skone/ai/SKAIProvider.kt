package io.skone.ai

import io.skone.common.result.SKResult

/**
 * Vendor-agnostic AI provider SPI.
 *
 * Concrete providers (Gemini, OpenAI, Claude, Ollama, Azure, custom) implement
 * this interface in separate modules or application code.
 *
 * @see docs/adr/0003-ai-provider-abstraction.md
 */
public interface SKAIProvider {
    /** Stable provider identifier, e.g. `openai`, `gemini`, `custom.acme`. */
    public val id: String

    /**
     * Executes a completion request.
     *
     * Implementations must not block the calling thread; use suspend / Dispatchers.IO.
     */
    public suspend fun complete(request: SKAIRequest): SKResult<SKAIResponse>
}

/**
 * AI configuration for [io.skone.SKOne].
 *
 * @property defaultProviderId Preferred provider id, or `null` to use the first registered.
 * @property providers Explicit provider instances (no reflection discovery).
 */
public data class SKAIConfig(
    public val defaultProviderId: String? = null,
    public val providers: List<SKAIProvider> = emptyList(),
) {
    public companion object {
        /** AI disabled — no providers registered. */
        public val Disabled: SKAIConfig = SKAIConfig()
    }
}

/**
 * High-level AI capability flags that components may request.
 */
public enum class SKAICapability {
    Grammar,
    Translation,
    Suggestions,
    VoiceInput,
    Summarization,
    Autofill,
    Chat,
    Image,
}

/**
 * Provider-agnostic AI request.
 *
 * @property prompt Primary text prompt.
 * @property systemInstruction Optional system / developer instruction.
 * @property capabilities Requested capabilities (providers may ignore unsupported ones).
 * @property metadata Opaque request metadata for providers / analytics.
 */
public data class SKAIRequest(
    public val prompt: String,
    public val systemInstruction: String? = null,
    public val capabilities: Set<SKAICapability> = emptySet(),
    public val metadata: Map<String, String> = emptyMap(),
)

/**
 * Provider-agnostic AI response.
 *
 * @property text Primary textual output.
 * @property providerId Provider that produced the response.
 * @property metadata Opaque response metadata.
 */
public data class SKAIResponse(
    public val text: String,
    public val providerId: String,
    public val metadata: Map<String, String> = emptyMap(),
)
