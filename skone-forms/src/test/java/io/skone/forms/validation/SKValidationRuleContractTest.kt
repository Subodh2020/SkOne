package io.skone.forms.validation

import io.skone.component.validation.SKValidationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Public contracts for stable [SKValidationRule] implementations.
 */
class SKValidationRuleContractTest {

    @Test
    fun requiredRuleFailsEmptyWithExpectedErrorStructure() {
        val result = SKRequiredRule().validate("")
        assertTrue(result is SKValidationResult.Invalid)
        val errors = (result as SKValidationResult.Invalid).errors
        assertEquals(1, errors.size)
        assertEquals("skone.forms.required", errors.first().code)
        assertEquals("Required", errors.first().message)
    }

    @Test
    fun requiredRulePassesNonEmptyValue() {
        assertTrue(SKRequiredRule().validate("Ada") is SKValidationResult.Valid)
    }

    @Test
    fun emailRuleAcceptsValidEmail() {
        assertTrue(SKEmailRule().validate("user@skone.io") is SKValidationResult.Valid)
    }

    @Test
    fun emailRuleRejectsInvalidEmailWithExpectedError() {
        val result = SKEmailRule().validate("not-an-email")
        assertTrue(result is SKValidationResult.Invalid)
        val error = (result as SKValidationResult.Invalid).errors.single()
        assertEquals("skone.forms.email", error.code)
        assertEquals("Invalid email", error.message)
    }

    @Test
    fun emailRuleTreatsEmptyAsValidLeavingRequiredToCallers() {
        // Empty is Valid so apps compose SKRequiredRule + SKEmailRule intentionally.
        assertTrue(SKEmailRule().validate("") is SKValidationResult.Valid)
        assertTrue(SKEmailRule().validate(null) is SKValidationResult.Valid)
    }

    @Test
    fun maxLengthRuleRejectsTooLongValues() {
        val result = SKMaxLengthRule(3).validate("abcd")
        assertTrue(result is SKValidationResult.Invalid)
        assertEquals("skone.forms.maxLength", (result as SKValidationResult.Invalid).errors.first().code)
    }

    @Test
    fun patternRuleRejectsNonMatchingValues() {
        val result = SKPatternRule(Regex("^\\d+$")).validate("abc")
        assertTrue(result is SKValidationResult.Invalid)
        assertEquals("skone.forms.pattern", (result as SKValidationResult.Invalid).errors.first().code)
    }

    @Test
    fun predicateRuleUsesCustomIdAndMessage() {
        val result = SKPredicateRule(
            id = "evenLength",
            message = "Length must be even",
            predicate = { value, _ -> (value?.toString()?.length ?: 0) % 2 == 0 },
        ).validate("abc")
        assertTrue(result is SKValidationResult.Invalid)
        val error = (result as SKValidationResult.Invalid).errors.single()
        assertEquals("skone.forms.evenLength", error.code)
        assertEquals("Length must be even", error.message)
    }

    @Test
    fun anyOfPassesWhenOneChildPasses() {
        val rule = SKAnyOfRule(
            listOf(
                SKRequiredRule(),
                SKEmailRule(),
            ),
        )
        // Empty fails Required but Email treats empty as Valid → AnyOf succeeds.
        assertTrue(rule.validate("") is SKValidationResult.Valid)
        assertTrue(rule.validate("user@skone.io") is SKValidationResult.Valid)
    }
}
