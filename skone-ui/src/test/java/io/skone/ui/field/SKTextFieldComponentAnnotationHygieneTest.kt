package io.skone.ui.field

import io.skone.common.annotation.SKInternal
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKRequiredRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Annotation hygiene: bridge plumbing is `@SKInternal`; [SKTextFieldComponent.create] stays OptIn-free.
 */
class SKTextFieldComponentAnnotationHygieneTest {

    @Test
    fun createDoesNotRequireOptInForConsumers() {
        val component = SKTextFieldComponent.create(
            id = "name",
            required = true,
            rules = listOf(SKRequiredRule()),
        )
        assertNotNull(component)
        assertEquals("name", component.id)
    }

    @Test
    @OptIn(SKInternal::class)
    fun bridgePlumbingCallableWithOptIn() {
        val form = SKFormController.create()
        val component = SKTextFieldComponent.create(id = "email")
        val field = component.toFormField()
        assertEquals("email", field.id)
        component.ensureRegistered(form)
        assertNotNull(form.registry.get("email"))
        component.ensureUnregistered(form)
        assertNull(form.registry.get("email"))
        val config = SKTextFieldComponent.defaultConfig(required = true)
        assertNotNull(config)
    }
}
