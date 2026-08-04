package io.skone.theme

/**
 * Preferred appearance mode for SKOne theming.
 *
 * Concrete Compose/XML application is performed by UI modules.
 */
public enum class SKThemeMode {
    /** Always use the light palette. */
    Light,

    /** Always use the dark palette. */
    Dark,

    /** Follow the platform system setting. */
    System,
}
