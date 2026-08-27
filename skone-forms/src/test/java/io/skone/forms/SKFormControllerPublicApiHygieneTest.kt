package io.skone.forms

import io.skone.component.validation.SKValidationResult
import io.skone.forms.field.SKFieldRegistry
import io.skone.forms.field.SKFormField
import io.skone.forms.validation.SKFormValidationResult
import io.skone.forms.validation.SKValidationContext
import io.skone.forms.validation.SKValidationEngine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

/**
 * Public API hygiene for [SKFormController.create] — no `@SKInternal` types in call sites.
 */
class SKFormControllerPublicApiHygieneTest {

    @Test
    fun createWithNoArgsDoesNotRequireInternalTypes() {
        val controller = SKFormController.create()
        assertNotNull(controller)
        controller.register(SKFormField(id = "email", initialValue = ""))
        assertEquals(1, controller.registry.all().size)
        controller.dispose()
    }

    @Test
    fun createAcceptsCustomPublicValidationEngine() {
        val custom = object : SKValidationEngine {
            override fun validateField(
                field: SKFormField,
                value: Any?,
                context: SKValidationContext,
            ): SKValidationResult = SKValidationResult.Valid

            override fun validateForm(registry: SKFieldRegistry): SKFormValidationResult =
                SKFormValidationResult.Empty
        }
        val controller = SKFormController.create(validation = custom)
        assertSame(custom, controller.validation)
        controller.dispose()
    }
}
