package io.skone.component.framework.animation

import io.skone.theme.tokens.SKDuration
import io.skone.theme.tokens.SKEasing
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Animation request resolved against theme motion tokens by UI bridges.
 *
 * Core never runs animations; Compose/XML interpret these requests.
 */
public data class SKAnimationRequest(
    public val componentId: String,
    public val name: String,
    public val duration: SKDuration,
    public val easing: SKEasing,
    public val metadata: Map<String, String> = emptyMap(),
)

/**
 * Animation manager interface for the component framework.
 */
public interface SKAnimationManager {
    /** Stream of animation requests for UI collectors. */
    public val requests: SharedFlow<SKAnimationRequest>

    /** Enqueues an animation request. */
    public fun request(request: SKAnimationRequest)

    /** Convenience using motion token keys from the active theme (resolved by UI). */
    public fun request(
        componentId: String,
        name: String,
        duration: SKDuration,
        easing: SKEasing,
    ) {
        request(SKAnimationRequest(componentId, name, duration, easing))
    }
}

/**
 * Default [SKAnimationManager] backed by a shared flow (replay = 0, extraBuffer = 16).
 */
public class SKDefaultAnimationManager : SKAnimationManager {
    private val _requests = MutableSharedFlow<SKAnimationRequest>(
        extraBufferCapacity = 16,
    )
    override val requests: SharedFlow<SKAnimationRequest> = _requests.asSharedFlow()

    override fun request(request: SKAnimationRequest) {
        _requests.tryEmit(request)
    }
}
