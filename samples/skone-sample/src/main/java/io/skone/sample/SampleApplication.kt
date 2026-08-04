package io.skone.sample

import android.app.Application
import io.skone.SKOne
import io.skone.SKOneConfig
import io.skone.sample.plugin.DemoPlugin

/**
 * Showcase application that initializes the SKOne foundation SDK.
 */
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SKOne.initialize(
            SKOneConfig(
                plugins = listOf(DemoPlugin()),
            ),
        )
    }
}
