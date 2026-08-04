package io.skone.component

import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.component.validation.SKValidator
import io.skone.component.validation.validateAll
import io.skone.theme.size.SKSize
import io.skone.theme.state.SKComponentState
import io.skone.theme.tokens.SKColorRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKComponentContractsTest {

    @Test
    fun `component config aggregates enabled from state and behavior`() {
        val config = SKComponentConfig(
            state = SKComponentState(enabled = true),
            behavior = io.skone.component.behavior.SKBehaviorConfig(enabled = false),
        )
        assertFalse(config.enabled)
    }

    @Test
    fun `appearance uses semantic roles not raw colors`() {
        val appearance = SKAppearanceConfig(
            size = SKSize.Large,
            containerColorRole = SKColorRole.Secondary,
            contentColorRole = SKColorRole.OnSecondary,
        )
        assertEquals(SKColorRole.Secondary, appearance.containerColorRole)
        assertEquals(SKSize.Large, appearance.size)
    }

    @Test
    fun `validateAll aggregates failures`() {
        val notBlank = SKValidator<String> { value ->
            if (value.isBlank()) {
                SKValidationResult.Invalid(SKValidationError("blank", "Required"))
            } else {
                SKValidationResult.Valid
            }
        }
        val minLength = SKValidator<String> { value ->
            if (value.length < 3) {
                SKValidationResult.Invalid(SKValidationError("min", "Too short"))
            } else {
                SKValidationResult.Valid
            }
        }

        val result = validateAll("ab", listOf(notBlank, minLength))
        assertTrue(result is SKValidationResult.Invalid)
        assertEquals(1, (result as SKValidationResult.Invalid).errors.size)
        assertEquals("min", result.errors.first().code)
    }
}
