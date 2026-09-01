package io.skone.compose.widget

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Applies [WindowInsets.safeDrawing] padding so chrome stays clear of system bars.
 *
 * Smallest shared inset helper for scaffolds and edge-to-edge hosts.
 * Not a full window/insets framework.
 */
@Composable
public fun Modifier.skSafeDrawingPadding(): Modifier =
    windowInsetsPadding(WindowInsets.safeDrawing)
