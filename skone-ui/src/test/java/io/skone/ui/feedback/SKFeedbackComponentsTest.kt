package io.skone.ui.feedback

import io.skone.component.appearance.SKAppearanceConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKFeedbackComponentsTest {

    @Test
    fun slider_snapsToStepsAndClamps() {
        val slider = SKSliderComponent.create(
            id = "s1",
            value = 0.4f,
            valueRange = 0f..1f,
            steps = 3,
        )
        assertEquals(0.5f, slider.coerceAndSnap(0.4f), 0.001f)
        slider.updateValue(2f, fromUser = true)
        assertEquals(1f, slider.value, 0.001f)
        slider.setEnabled(false)
        assertFalse(slider.interactive)
        slider.updateValue(0f, fromUser = true)
        assertEquals(1f, slider.value, 0.001f)
    }

    @Test
    fun progress_clampsAndTogglesIndeterminate() {
        val progress = SKProgressIndicatorComponent.create(
            id = "p1",
            progress = 1.5f,
            style = SKProgressStyle.Circular,
        )
        assertEquals(1f, progress.progress, 0.001f)
        assertEquals(SKProgressStyle.Circular, progress.style)
        assertEquals(SKAppearanceConfig.Progress.containerColorRole, progress.config.appearance.containerColorRole)
        progress.setIndeterminate(true)
        assertTrue(progress.indeterminate)
        progress.setProgress(-1f)
        assertEquals(0f, progress.progress, 0.001f)
    }
}
