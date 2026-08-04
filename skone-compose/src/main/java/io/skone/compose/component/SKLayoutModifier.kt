package io.skone.compose.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.skone.component.framework.layout.SKLayoutMode
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.compose.theme.toDp

/**
 * Maps a framework [SKLayoutSpec] to a Compose [Modifier].
 *
 * Used by future widgets — not a widget itself.
 */
public fun Modifier.skLayout(spec: SKLayoutSpec): Modifier {
    var result = this
    result = when (spec.width.mode) {
        SKLayoutMode.Wrap -> result
        SKLayoutMode.Fill -> result.fillMaxWidth()
        SKLayoutMode.Exact -> {
            val exact = spec.width.exact?.toDp() ?: 0.dp
            result.width(exact)
        }
    }
    result = when (spec.height.mode) {
        SKLayoutMode.Wrap -> result
        SKLayoutMode.Fill -> result.fillMaxHeight()
        SKLayoutMode.Exact -> {
            val exact = spec.height.exact?.toDp() ?: 0.dp
            result.height(exact)
        }
    }
    result = result.padding(
        start = spec.padding.start.toDp(),
        top = spec.padding.top.toDp(),
        end = spec.padding.end.toDp(),
        bottom = spec.padding.bottom.toDp(),
    )
    return result
}
