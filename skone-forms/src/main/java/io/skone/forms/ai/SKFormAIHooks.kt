package io.skone.forms.ai

import io.skone.SKOne
import io.skone.common.annotation.SKInternal
import io.skone.ai.SKAICapability
import io.skone.ai.SKAIRequest
import io.skone.ai.SKAIResponse
import io.skone.common.result.SKResult
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.ai.SKAITrigger
import io.skone.forms.field.SKFormField

/**
 * AI integration hooks for form fields and whole-form assist.
 *
 * Does not embed vendor SDKs — delegates to [SKOne.aiComplete].
 */
public interface SKFormAIHooks {
    /**
     * Runs AI for a single [field] using its [SKFormField.ai] config and current [value].
     */
    public suspend fun assistField(
        field: SKFormField,
        value: Any?,
        promptOverride: String? = null,
    ): SKResult<SKAIResponse>

    /**
     * Runs a form-level AI request (e.g. autofill suggestions across fields).
     */
    public suspend fun assistForm(
        values: Map<String, Any?>,
        prompt: String,
        capabilities: Set<SKAICapability> = setOf(SKAICapability.Autofill, SKAICapability.Suggestions),
        providerId: String? = null,
    ): SKResult<SKAIResponse>

    /**
     * Whether [trigger] should run AI for [field].
     */
    public fun shouldTrigger(field: SKFormField, trigger: SKAITrigger): Boolean
}

/**
 * Default [SKFormAIHooks] using [SKOne].
 *
 * **Internal implementation** — not intended for application use.
 */
@SKInternal
public class SKDefaultFormAIHooks : SKFormAIHooks {
    override suspend fun assistField(
        field: SKFormField,
        value: Any?,
        promptOverride: String?,
    ): SKResult<SKAIResponse> {
        val ai = field.ai
        if (ai == null || !ai.enabled) {
            return SKResult.failure(
                code = io.skone.common.error.SKError.CODE_AI_UNAVAILABLE,
                message = "AI is not configured for field '${field.id}'",
            )
        }
        val template = promptOverride ?: ai.promptTemplate ?: "Assist with field {id}: {value}"
        val prompt = template
            .replace("{id}", field.id)
            .replace("{value}", value?.toString().orEmpty())
        return SKOne.aiComplete(
            request = SKAIRequest(
                prompt = prompt,
                capabilities = ai.capabilities,
                metadata = ai.metadata + mapOf("fieldId" to field.id),
            ),
            providerId = ai.providerId,
        )
    }

    override suspend fun assistForm(
        values: Map<String, Any?>,
        prompt: String,
        capabilities: Set<SKAICapability>,
        providerId: String?,
    ): SKResult<SKAIResponse> = SKOne.aiComplete(
        request = SKAIRequest(
            prompt = prompt,
            capabilities = capabilities,
            metadata = mapOf("formValues" to values.keys.joinToString(",")),
        ),
        providerId = providerId,
    )

    override fun shouldTrigger(field: SKFormField, trigger: SKAITrigger): Boolean {
        val ai: SKAIComponentConfig = field.ai ?: return false
        return ai.enabled && trigger in ai.triggers
    }
}
