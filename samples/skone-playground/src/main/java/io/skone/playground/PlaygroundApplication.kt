package io.skone.playground

import android.app.Application
import io.skone.SKOne
import io.skone.SKOneConfig

/**
 * Official SKOne developer playground application.
 */
class PlaygroundApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SKOne.initialize(SKOneConfig())
    }
}
