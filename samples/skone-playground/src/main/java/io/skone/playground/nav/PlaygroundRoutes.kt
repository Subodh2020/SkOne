package io.skone.playground.nav

/**
 * Top-level and nested routes for the playground.
 */
object PlaygroundRoutes {
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val GALLERY = "gallery"
    const val SEARCH = "search"
    const val DOCS = "docs"
    const val SAMPLES = "samples"
    const val WIDGET = "widget/{widgetId}"
    const val DOC_ARTICLE = "docs/{docId}"
    const val SAMPLE_DETAIL = "samples/{sampleId}"

    fun widget(widgetId: String): String = "widget/$widgetId"
    fun doc(docId: String): String = "docs/$docId"
    fun sample(sampleId: String): String = "samples/$sampleId"
}

/**
 * Bottom destinations shown in the playground shell.
 */
enum class PlaygroundTab(
    val route: String,
    val label: String,
) {
    Home(PlaygroundRoutes.HOME, "Home"),
    Catalog(PlaygroundRoutes.CATALOG, "Catalog"),
    Gallery(PlaygroundRoutes.GALLERY, "Gallery"),
    Samples(PlaygroundRoutes.SAMPLES, "Samples"),
    Docs(PlaygroundRoutes.DOCS, "Docs"),
}
