package io.skone.component.framework

import io.skone.common.log.SKNoOpLogger
import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.framework.analytics.SKRecordingAnalyticsHook
import io.skone.component.framework.base.SKBaseInputComponent
import io.skone.component.framework.base.SKBaseInteractiveComponent
import io.skone.component.framework.base.SKBaseNavigationComponent
import io.skone.component.framework.base.SKBaseSelectableComponent
import io.skone.component.framework.dsl.skComponent
import io.skone.component.framework.event.SKComponentEvent
import io.skone.component.framework.plugin.SKComponentPlugin
import io.skone.component.validation.SKValidationConfig
import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.component.validation.SKValidator
import io.skone.theme.size.SKSize
import io.skone.theme.state.SKComponentState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKComponentFrameworkTest {

    @Test
    fun `runtime attach detach and events`() {
        val analytics = SKRecordingAnalyticsHook()
        val runtime = SKComponentRuntime.create(
            logger = SKNoOpLogger,
            analytics = analytics,
        )
        val events = mutableListOf<SKComponentEvent>()
        runtime.events.subscribe { events += it }

        val component = object : SKBaseInteractiveComponent(
            id = "c1",
            componentType = "Test",
            config = SKComponentConfig(
                analytics = SKAnalyticsConfig(componentName = "Test"),
            ),
        ) {}

        component.attach(runtime)
        assertTrue(component.isAttached)
        component.performClick()
        component.detach()
        assertFalse(component.isAttached)

        assertTrue(events.any { it is SKComponentEvent.Attached })
        assertTrue(events.any { it is SKComponentEvent.Clicked })
        assertTrue(events.any { it is SKComponentEvent.Detached })
        assertTrue(analytics.entries.isNotEmpty())
    }

    @Test
    fun `focus manager tracks ownership`() = runTest {
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        runtime.focus.requestFocus("a")
        assertEquals("a", runtime.focus.focusedId.first())
        runtime.focus.requestFocus("b")
        assertEquals("b", runtime.focus.focusedId.first())
        runtime.focus.clearFocus("b")
        assertNull(runtime.focus.focusedId.first())
    }

    @Test
    fun `input validation required and custom validator`() {
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        val minLen = SKValidator<String> { v ->
            if (v.length >= 3) SKValidationResult.Valid
            else SKValidationResult.Invalid(SKValidationError("min", "Too short"))
        }
        val input = object : SKBaseInputComponent<String>(
            id = "email",
            componentType = "TestInput",
            initialValue = "",
            config = SKComponentConfig(validation = SKValidationConfig.Required),
            validators = listOf(minLen),
        ) {}

        input.attach(runtime)
        val required = input.validate()
        assertTrue(required is SKValidationResult.Invalid)

        input.setValue("ab", fromUser = true)
        val short = input.validate()
        assertTrue(short is SKValidationResult.Invalid)

        input.setValue("abcd", fromUser = true)
        val ok = input.validate()
        assertTrue(ok is SKValidationResult.Valid)
        input.detach()
    }

    @Test
    fun `selectable multi and single`() {
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        val single = object : SKBaseSelectableComponent<String>(
            id = "s1",
            componentType = "Select",
            multiSelect = false,
        ) {}
        single.attach(runtime)
        single.select("a")
        single.select("b")
        assertEquals(setOf("b"), single.selection)
        assertEquals("b", single.selected)

        val multi = object : SKBaseSelectableComponent<String>(
            id = "s2",
            componentType = "Select",
            multiSelect = true,
        ) {}
        multi.attach(runtime)
        multi.select("a")
        multi.select("b")
        assertEquals(setOf("a", "b"), multi.selection)
        multi.detach()
        single.detach()
    }

    @Test
    fun `navigation dispatches route event`() {
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        val events = mutableListOf<SKComponentEvent>()
        runtime.events.subscribe { events += it }

        val nav = object : SKBaseNavigationComponent(
            id = "nav",
            componentType = "Nav",
            destination = SKNavigationDestination(route = "home/detail"),
        ) {}
        nav.attach(runtime)
        nav.navigate()
        assertTrue(
            events.any {
                it is SKComponentEvent.NavigationRequested && it.route == "home/detail"
            },
        )
        nav.detach()
    }

    @Test
    fun `component plugin receives attach hooks`() {
        var attached = 0
        val plugin = object : SKComponentPlugin {
            override fun onComponentAttached(runtime: SKComponentRuntime, component: SKComponent) {
                attached++
            }
        }
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger, plugins = listOf(plugin))
        val component = object : SKBaseInteractiveComponent("p1", "Test") {}
        component.attach(runtime)
        assertEquals(1, attached)
        component.detach()
    }

    @Test
    fun `dsl builds config and layout`() {
        val spec = skComponent {
            state = SKComponentState(enabled = true, error = false)
            appearance = appearance.copy(size = SKSize.Large)
            validation = SKValidationConfig.Required
            supportingText = "Helper"
            layout = io.skone.component.framework.layout.SKLayoutSpec.FillWidth
        }
        assertEquals(SKSize.Large, spec.config.appearance.size)
        assertTrue(spec.config.required)
        assertEquals("Helper", spec.config.supportingText)
        assertEquals(
            io.skone.component.framework.layout.SKLayoutMode.Fill,
            spec.layout.width.mode,
        )
    }

    @Test
    fun `state manager updates observable state`() = runTest {
        val runtime = SKComponentRuntime.create(logger = SKNoOpLogger)
        runtime.state.updateState("x") { it.copy(loading = true) }
        assertTrue(runtime.state.observe("x").first().loading)
    }
}
