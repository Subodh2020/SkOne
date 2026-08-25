@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.forms

import app.cash.turbine.test
import io.skone.component.validation.SKValidationResult
import io.skone.forms.field.SKFormField
import io.skone.forms.formatter.SKTrimFormatter
import io.skone.forms.mask.SKDefaultInputMaskEngine
import io.skone.forms.mask.SKInputMasks
import io.skone.forms.state.SKFormLifecycle
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKMinLengthRule
import io.skone.forms.validation.SKRequiredRule
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKFormControllerTest {

    @Test
    fun `register update validate and submit happy path`() = runTest {
        val controller = SKFormController.create()
        controller.register(
            SKFormField(
                id = "email",
                initialValue = "",
                rules = listOf(SKRequiredRule(), SKEmailRule()),
                formatter = SKTrimFormatter,
            ),
        )

        controller.updateRawInput("email", "  user@example.com  ")
        assertEquals("user@example.com", controller.values()["email"])
        assertTrue(controller.state.value.isDirty)

        val result = controller.submit()
        assertTrue(result.isValid)
        assertEquals(SKFormLifecycle.Submitted, controller.state.value.lifecycle)
        controller.dispose()
    }

    @Test
    fun `submit fails when required empty`() {
        val controller = SKFormController.create()
        controller.register(
            SKFormField(
                id = "name",
                initialValue = "",
                rules = listOf(SKRequiredRule(), SKMinLengthRule(2)),
            ),
        )
        val result = controller.submit()
        assertFalse(result.isValid)
        assertEquals(SKFormLifecycle.Invalid, controller.state.value.lifecycle)
        assertTrue(controller.errors.hasErrors())
        controller.dispose()
    }

    @Test
    fun `focus chain next and previous`() {
        val controller = SKFormController.create()
        controller.register(SKFormField(id = "a"))
        controller.register(SKFormField(id = "b"))
        controller.register(SKFormField(id = "c"))

        controller.requestFocus("a")
        assertEquals("b", controller.focus.focusNext("a"))
        assertEquals("c", controller.focus.focusNext("b"))
        assertEquals("b", controller.focus.focusPrevious("c"))
        controller.dispose()
    }

    @Test
    fun `reset restores initial values`() {
        val controller = SKFormController.create()
        controller.register(SKFormField(id = "x", initialValue = "init"))
        controller.updateValue("x", "changed")
        controller.reset()
        assertEquals("init", controller.values()["x"])
        assertFalse(controller.state.value.isDirty)
        assertEquals(SKFormLifecycle.Ready, controller.state.value.lifecycle)
        controller.dispose()
    }

    @Test
    fun `state flow emits lifecycle changes`() = runTest {
        val controller = SKFormController.create()
        controller.state.test {
            val initial = awaitItem()
            assertEquals(SKFormLifecycle.Ready, initial.lifecycle)

            controller.register(SKFormField(id = "f", initialValue = ""))
            val afterRegister = awaitItem()
            assertEquals(1, afterRegister.fieldCount)

            controller.updateValue("f", "v")
            val dirty = awaitItem()
            assertEquals(SKFormLifecycle.Dirty, dirty.lifecycle)
            cancelAndIgnoreRemainingEvents()
        }
        controller.dispose()
    }
}

class SKInputMaskEngineTest {
    private val engine = SKDefaultInputMaskEngine()

    @Test
    fun `us phone mask formats progressive input`() {
        val masked = engine.apply(SKInputMasks.UsPhone, "5551234567")
        assertEquals("(555) 123-4567", masked.display)
        assertEquals("5551234567", masked.raw)
        assertTrue(masked.complete)
    }

    @Test
    fun `incomplete mask leaves placeholders`() {
        val masked = engine.apply(SKInputMasks.UsPhone, "55")
        assertTrue(masked.display.startsWith("(55"))
        assertFalse(masked.complete)
    }
}

class SKValidationRulesTest {
    @Test
    fun `email rule accepts and rejects`() {
        assertTrue(SKEmailRule().validate("a@b.co") is SKValidationResult.Valid)
        assertTrue(SKEmailRule().validate("nope") is SKValidationResult.Invalid)
    }

    @Test
    fun `allOf aggregates errors`() {
        val rule = io.skone.forms.validation.SKAllOfRule(
            listOf(SKRequiredRule(), SKMinLengthRule(3)),
        )
        val result = rule.validate("")
        assertTrue(result is SKValidationResult.Invalid)
    }
}
