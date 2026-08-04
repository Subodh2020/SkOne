package io.skone.theme.tokens

/**
 * Motion duration and easing tokens.
 *
 * @see docs/DESIGN_SYSTEM.md
 */
public interface SKMotionTokens {
    public val short1: SKDuration
    public val short2: SKDuration
    public val short3: SKDuration
    public val short4: SKDuration

    public val medium1: SKDuration
    public val medium2: SKDuration
    public val medium3: SKDuration
    public val medium4: SKDuration

    public val long1: SKDuration
    public val long2: SKDuration
    public val long3: SKDuration
    public val long4: SKDuration

    public val standard: SKEasing
    public val emphasized: SKEasing
    public val decelerated: SKEasing
    public val accelerated: SKEasing
}
