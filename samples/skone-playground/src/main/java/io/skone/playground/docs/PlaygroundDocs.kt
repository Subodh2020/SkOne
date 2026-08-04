package io.skone.playground.docs

import io.skone.playground.catalog.CatalogId

/**
 * Bundled documentation articles for the in-app docs site.
 */
data class DocArticle(
    val id: CatalogId,
    val title: String,
    val body: String,
)

object PlaygroundDocs {
    val articles: List<DocArticle> = listOf(
        DocArticle(
            id = CatalogId.DocOverview,
            title = "SDK Overview",
            body = """
                SKOne is a modular Android developer platform (Kotlin, API 24+).

                Modules
                • skone-bom — Bill of Materials
                • skone-theme — Design tokens + theme engine
                • skone-core — Component framework
                • skone-forms — Form controller & engines
                • skone-ui — Shared widget contracts
                • skone-compose / skone-xml — UI bridges + widgets

                Consume via the BOM. Prefer design tokens — never hardcode colors,
                typography, spacing, or dimensions in production widgets.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocPlayground,
            title = "Playground Guide",
            body = """
                This app is the official showcase for every future SKOne component.

                • Catalog — browse widgets, frameworks, samples, docs
                • Gallery — live previews
                • Editors — tweak properties and preview
                • Codegen — copy Compose or XML
                • Theme switcher — Light / Dark / System
                • Search — filter the catalog
                • Samples — integration recipes

                When you add a production widget, register it in PlaygroundCatalog
                and wire gallery + editor + codegen.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkText,
            title = "SKText",
            body = """
                SKText / SKTextView is the reference display widget.

                Compose: io.skone.compose.widget.SKText
                XML: io.skone.xml.widget.SKTextView

                Visuals resolve through SKAppearanceConfig + theme tokens.
                Supports annotated spans, overflow, alignment, and accessibility.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkTextField,
            title = "SKTextField",
            body = """
                SKTextField / SKTextFieldView is the flagship input.

                Auto-registers with SKFormController when provided.
                Uses ValidationEngine, FormatterEngine, InputMask, and FocusChain.
                Supports label, hint, supportingText, icons, masks, IME, error/success.
            """.trimIndent(),
        ),
    )

    fun find(id: CatalogId): DocArticle? = articles.firstOrNull { it.id == id }
}
