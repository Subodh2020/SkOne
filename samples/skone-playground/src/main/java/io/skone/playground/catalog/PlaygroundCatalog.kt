package io.skone.playground.catalog

/**
 * Kind of catalog entry used for filtering and navigation.
 */
enum class CatalogKind {
    Widget,
    Framework,
    Sample,
    Doc,
}

/**
 * Stable catalog id used in navigation routes.
 */
enum class CatalogId(val route: String) {
    SkText("sktext"),
    SkTextField("sktextfield"),
    FormFramework("form-framework"),
    ComponentFramework("component-framework"),
    DesignSystem("design-system"),
    SampleForm("sample-form"),
    SampleXml("sample-xml"),
    DocOverview("doc-overview"),
    DocPlayground("doc-playground"),
    DocSkText("doc-sktext"),
    DocSkTextField("doc-sktextfield"),
}

/**
 * Single searchable entry in the official SKOne showcase catalog.
 *
 * Future production widgets must add an entry here.
 */
data class CatalogEntry(
    val id: CatalogId,
    val title: String,
    val subtitle: String,
    val kind: CatalogKind,
    val tags: List<String>,
    val description: String,
)

/**
 * Registry of everything discoverable in the playground.
 */
object PlaygroundCatalog {
    val entries: List<CatalogEntry> = listOf(
        CatalogEntry(
            id = CatalogId.SkText,
            title = "SKText",
            subtitle = "Display text widget",
            kind = CatalogKind.Widget,
            tags = listOf("compose", "xml", "text", "typography", "a11y"),
            description = "Reference display widget. Token-driven typography and color roles.",
        ),
        CatalogEntry(
            id = CatalogId.SkTextField,
            title = "SKTextField",
            subtitle = "Flagship input widget",
            kind = CatalogKind.Widget,
            tags = listOf("compose", "xml", "input", "form", "validation", "mask"),
            description = "Flagship input with form auto-register, masks, formatters, and IME.",
        ),
        CatalogEntry(
            id = CatalogId.FormFramework,
            title = "Form Framework",
            subtitle = "SKFormController",
            kind = CatalogKind.Framework,
            tags = listOf("forms", "validation", "focus", "formatter", "mask"),
            description = "Controller, ValidationEngine, FormatterEngine, InputMask, FocusChain.",
        ),
        CatalogEntry(
            id = CatalogId.ComponentFramework,
            title = "Component Framework",
            subtitle = "Lifecycle & plugins",
            kind = CatalogKind.Framework,
            tags = listOf("component", "runtime", "analytics", "plugins", "ai"),
            description = "Shared contracts, runtime, events, validation, and AI hooks.",
        ),
        CatalogEntry(
            id = CatalogId.DesignSystem,
            title = "Design System",
            subtitle = "Tokens & themes",
            kind = CatalogKind.Framework,
            tags = listOf("theme", "tokens", "light", "dark"),
            description = "Design tokens, theme engine, appearance roles — no hardcoded visuals.",
        ),
        CatalogEntry(
            id = CatalogId.SampleForm,
            title = "Form + SKTextField",
            subtitle = "Integration sample",
            kind = CatalogKind.Sample,
            tags = listOf("sample", "form", "email", "phone"),
            description = "Email and phone fields auto-registered with SKFormController.",
        ),
        CatalogEntry(
            id = CatalogId.SampleXml,
            title = "XML widgets",
            subtitle = "Views integration",
            kind = CatalogKind.Sample,
            tags = listOf("sample", "xml", "views"),
            description = "SKTextView and SKTextFieldView hosted in AndroidView.",
        ),
        CatalogEntry(
            id = CatalogId.DocOverview,
            title = "SDK Overview",
            subtitle = "Getting started",
            kind = CatalogKind.Doc,
            tags = listOf("docs", "guide"),
            description = "Modules, versioning, and how to consume SKOne.",
        ),
        CatalogEntry(
            id = CatalogId.DocPlayground,
            title = "Playground Guide",
            subtitle = "Developer experience",
            kind = CatalogKind.Doc,
            tags = listOf("docs", "playground", "dx"),
            description = "How to use catalog, gallery, editors, and codegen.",
        ),
        CatalogEntry(
            id = CatalogId.DocSkText,
            title = "SKText docs",
            subtitle = "Widget reference",
            kind = CatalogKind.Doc,
            tags = listOf("docs", "sktext"),
            description = "API surface and usage for SKText / SKTextView.",
        ),
        CatalogEntry(
            id = CatalogId.DocSkTextField,
            title = "SKTextField docs",
            subtitle = "Widget reference",
            kind = CatalogKind.Doc,
            tags = listOf("docs", "sktextfield", "input"),
            description = "API surface and usage for SKTextField / SKTextFieldView.",
        ),
    )

    fun widgets(): List<CatalogEntry> = entries.filter { it.kind == CatalogKind.Widget }

    fun samples(): List<CatalogEntry> = entries.filter { it.kind == CatalogKind.Sample }

    fun docs(): List<CatalogEntry> = entries.filter { it.kind == CatalogKind.Doc }

    fun find(id: CatalogId): CatalogEntry? = entries.firstOrNull { it.id == id }

    fun search(query: String): List<CatalogEntry> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return entries
        return entries.filter { entry ->
            entry.title.lowercase().contains(q) ||
                entry.subtitle.lowercase().contains(q) ||
                entry.description.lowercase().contains(q) ||
                entry.tags.any { it.contains(q) } ||
                entry.kind.name.lowercase().contains(q)
        }
    }
}
