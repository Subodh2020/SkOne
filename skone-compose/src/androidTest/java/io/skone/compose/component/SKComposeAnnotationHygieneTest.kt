package io.skone.compose.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.common.annotation.SKInternal
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.compose.theme.SKTheme
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.theme.SKThemeMode
import io.skone.ui.text.SKTextComponent
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Annotation hygiene for Compose plumbing:
 * - Flagship APIs require no `@OptIn(SKInternal)`.
 * - Internal helpers are callable with `@OptIn`.
 */
@RunWith(AndroidJUnit4::class)
class SKComposeAnnotationHygieneTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun flagshipApisDoNotRequireOptIn() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                val runtime = rememberSKComponentRuntime()
                ProvideSKComponentRuntime(runtime) {
                    var value by remember { mutableStateOf("") }
                    SKText(text = "Hello")
                    SKTextField(
                        value = value,
                        onValueChange = { value = it },
                        fieldId = "hygiene",
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    @OptIn(SKInternal::class)
    fun internalPlumbingCallableWithOptIn() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                val runtime = rememberSKComponentRuntime()
                ProvideSKComponentRuntime(runtime) {
                    Box(modifier = Modifier.skLayout(SKLayoutSpec.FillWidth))
                    val component = rememberSKComponent(runtime) {
                        SKTextComponent.create(id = "optin_probe", text = "probe")
                    }
                    assertNotNull(component)
                    SKComponentLifecycle(component = component, runtime = runtime)
                }
            }
        }
        composeRule.waitForIdle()
    }
}
