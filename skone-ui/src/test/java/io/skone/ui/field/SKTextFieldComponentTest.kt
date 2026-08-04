package io.skone.ui.field

import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.forms.SKFormController
import io.skone.forms.validation.SKRequiredRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKTextFieldComponentTest {

    @Test
    fun `create sets defaults and label`() {
        val component = SKTextFieldComponent.create(
            id = "email",
            initialValue = "",
            label = "Email",
            hint = "you@skone.io",
            required = true,
        )
        assertEquals("email", component.id)
        assertEquals("Email", component.label)
        assertEquals("you@skone.io", component.hint)
        assertTrue(component.config.required)
        assertEquals(SKAppearanceConfig.TextField.outlineColorRole, component.config.appearance.outlineColorRole)
        assertEquals(SKFieldVisualState.None, component.visualState)
    }

    @Test
    fun `toFormField includes required rule when required`() {
        val component = SKTextFieldComponent.create(id = "f1", required = true)
        val field = component.toFormField()
        assertTrue(field.rules.any { it.id == "required" })
    }

    @Test
    fun `ensureRegistered auto-registers with form controller`() {
        val form = SKFormController.create()
        val component = SKTextFieldComponent.create(
            id = "phone",
            rules = listOf(SKRequiredRule()),
        )
        component.ensureRegistered(form)
        assertNotNull(form.registry.get("phone"))
        component.ensureRegistered(form) // idempotent
        assertEquals(1, form.registry.all().size)
        component.ensureUnregistered(form)
        assertEquals(null, form.registry.get("phone"))
        form.dispose()
    }

    @Test
    fun `applyValidationResult updates visual and supporting text`() {
        val component = SKTextFieldComponent.create(id = "f2", supportingText = "Help")
        component.applyValidationResult(
            SKValidationResult.Invalid(SKValidationError("required", "Required")),
        )
        assertEquals(SKFieldVisualState.Error, component.visualState)
        assertEquals("Required", component.fieldSupportingText)

        component.applyValidationResult(SKValidationResult.Valid)
        assertEquals(SKFieldVisualState.Success, component.visualState)
    }

    @Test
    fun `resolvedAppearance switches for error and success`() {
        val component = SKTextFieldComponent.create(id = "f3")
        assertEquals(SKAppearanceConfig.TextField, component.resolvedAppearance())

        component.setVisualState(SKFieldVisualState.Error)
        assertEquals(SKAppearanceConfig.TextFieldError.outlineColorRole, component.resolvedAppearance().outlineColorRole)

        component.setVisualState(SKFieldVisualState.Success)
        assertEquals(SKAppearanceConfig.TextFieldSuccess.outlineColorRole, component.resolvedAppearance().outlineColorRole)
    }

    @Test
    fun `setters update mutable fields`() {
        val component = SKTextFieldComponent.create(id = "f4")
        component.setLabel("L")
        component.setHint("H")
        component.setSupportingText("S")
        component.setImeAction(SKImeAction.Next)
        component.setKeyboardType(SKKeyboardType.Email)
        component.setSingleLine(false)
        component.setMaxLines(3)

        assertEquals("L", component.label)
        assertEquals("H", component.hint)
        assertEquals("S", component.fieldSupportingText)
        assertEquals(SKImeAction.Next, component.imeAction)
        assertEquals(SKKeyboardType.Email, component.keyboardType)
        assertFalse(component.singleLine)
        assertEquals(3, component.maxLines)
    }
}
