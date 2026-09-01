package io.skone.xml.widget

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Applies system-bar insets as padding so scaffold chrome clears status/nav areas.
 *
 * Smallest XML counterpart to Compose [io.skone.compose.widget.skSafeDrawingPadding].
 * Not a full window/insets framework.
 */
public fun View.skApplySystemBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
    ViewCompat.requestApplyInsets(this)
}
