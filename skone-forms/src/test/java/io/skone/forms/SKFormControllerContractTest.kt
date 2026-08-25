package io.skone.forms

import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.forms.field.SKFormField
import io.skone.forms.state.SKFormLifecycle
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKRequiredRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Public consumer contracts for [SKFormController] beyond the existing smoke suite.
 */
class SKFormControllerContractTest {

    @Test
    fun createStartsReadyWithNoFields() {
        val controller = SKFormController.create()
        assertEquals(SKFormLifecycle.Ready, controller.state.value.lifecycle)
        assertEquals(0, controller.state.value.fieldCount)
        assertTrue(controller.registry.all().isEmpty())
        assertTrue(controller.values().isEmpty())
        assertFalse(controller.errors.hasErrors())
        controller.dispose()
    }

    @Test
    fun registerAndUnregisterUpdateRegistryAndState() {
        val controller = SKFormController.create()
        controller.register(SKFormField(id = "email", initialValue = ""))
        controller.register(SKFormField(id = "name", initialValue = ""))

        assertEquals(2, controller.state.value.fieldCount)
        assertNotNull(controller.registry.get("email"))
        assertNotNull(controller.registry.get("name"))
        assertEquals(2, controller.registry.all().size)

        controller.unregister("email")
        assertNull(controller.registry.get("email"))
        assertNotNull(controller.registry.get("name"))
        assertEquals(1, controller.state.value.fieldCount)
        assertEquals(1, controller.registry.all().size)

        controller.dispose()
    }

    @Test
    fun multiFieldValidateAssociatesErrorsWithCorrectFields() {
        val controller = SKFormController.create()
        controller.register(
            SKFormField(
                id = "name",
                initialValue = "Ada",
                rules = listOf(SKRequiredRule()),
            ),
        )
        controller.register(
            SKFormField(
                id = "email",
                initialValue = "not-an-email",
                rules = listOf(SKRequiredRule(), SKEmailRule()),
            ),
        )

        val result = controller.validate()

        assertFalse(result.isValid)
        assertTrue(result.fieldResults["name"] is SKValidationResult.Valid)
        assertTrue(result.fieldResults["email"] is SKValidationResult.Invalid)

        assertTrue(controller.errors.errorsFor("name").isEmpty())
        val emailErrors = controller.errors.errorsFor("email")
        assertTrue(emailErrors.isNotEmpty())
        assertEquals("skone.forms.email", emailErrors.first().code)
        assertEquals("Invalid email", emailErrors.first().message)

        // Valid field must not inherit the other field's errors.
        assertNull(controller.errors.errors.value["name"])
        assertTrue(controller.errors.errors.value.containsKey("email"))

        controller.dispose()
    }

    @Test
    fun submitWithInvalidValuesReturnsFieldSpecificErrors() {
        val controller = SKFormController.create()
        controller.register(
            SKFormField(id = "name", initialValue = "", rules = listOf(SKRequiredRule())),
        )
        controller.register(
            SKFormField(
                id = "email",
                initialValue = "bad",
                rules = listOf(SKEmailRule()),
            ),
        )

        controller.updateValue("name", "")
        controller.updateValue("email", "bad")

        val result = controller.submit()

        assertFalse(result.isValid)
        assertEquals(SKFormLifecycle.Invalid, controller.state.value.lifecycle)
        assertTrue(controller.errors.hasErrors())

        val nameErrors = controller.errors.errorsFor("name")
        assertEquals(listOf(SKValidationError("skone.forms.required", "Required")), nameErrors)

        val emailErrors = controller.errors.errorsFor("email")
        assertEquals(listOf(SKValidationError("skone.forms.email", "Invalid email")), emailErrors)

        assertTrue(result.fieldResults["name"] is SKValidationResult.Invalid)
        assertTrue(result.fieldResults["email"] is SKValidationResult.Invalid)

        controller.dispose()
    }

    @Test
    fun submitSucceedsWhenAllFieldsValid() {
        val controller = SKFormController.create()
        controller.register(
            SKFormField(id = "name", initialValue = "", rules = listOf(SKRequiredRule())),
        )
        controller.register(
            SKFormField(
                id = "email",
                initialValue = "",
                rules = listOf(SKRequiredRule(), SKEmailRule()),
            ),
        )

        controller.updateValue("name", "Ada")
        controller.updateValue("email", "ada@skone.io")

        val result = controller.submit()

        assertTrue(result.isValid)
        assertEquals(SKFormLifecycle.Submitted, controller.state.value.lifecycle)
        assertFalse(controller.errors.hasErrors())
        assertEquals("Ada", controller.values()["name"])
        assertEquals("ada@skone.io", controller.values()["email"])
        assertTrue(result.fieldResults.values.all { it is SKValidationResult.Valid })

        controller.dispose()
    }

    @Test
    fun correctingInvalidValueAndRevalidatingClearsFieldError() {
        val controller = SKFormController.create()
        controller.register(
            SKFormField(
                id = "email",
                initialValue = "",
                rules = listOf(SKRequiredRule(), SKEmailRule()),
            ),
        )

        assertEquals("", controller.registry.state("email")?.value ?: "")

        controller.updateValue("email", "not-an-email")
        assertEquals("not-an-email", controller.values()["email"])

        val invalid = controller.validate()
        assertFalse(invalid.isValid)
        assertTrue(controller.errors.errorsFor("email").isNotEmpty())

        controller.updateValue("email", "user@skone.io")
        val valid = controller.validate()

        assertTrue(valid.isValid)
        assertTrue(controller.errors.errorsFor("email").isEmpty())
        assertFalse(controller.errors.hasErrors())
        assertTrue(valid.fieldResults["email"] is SKValidationResult.Valid)

        controller.dispose()
    }
}
