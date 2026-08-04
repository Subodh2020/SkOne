package io.skone.playground.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toDp
import io.skone.playground.catalog.CatalogId
import io.skone.playground.catalog.PlaygroundCatalog
import io.skone.playground.docs.PlaygroundDocs

@Composable
fun DocsScreen(
    onOpenDoc: (String) -> Unit,
) {
    val spacing = skTheme.tokens.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.sm.toDp()),
    ) {
        PlaygroundSectionTitle("Documentation")
        PlaygroundBody(
            "In-app docs site. Full markdown lives under /docs; static site under /docs-site.",
        )
        PlaygroundCatalog.docs().forEach { entry ->
            CatalogEntryCard(entry = entry, onClick = { onOpenDoc(entry.id.route) })
        }
        PlaygroundCatalog.entries
            .filter { it.kind == io.skone.playground.catalog.CatalogKind.Framework }
            .forEach { entry ->
                CatalogEntryCard(
                    entry = entry,
                    onClick = {
                        // Framework entries map to overview-style articles when available
                        onOpenDoc(CatalogId.DocOverview.route)
                    },
                )
            }
    }
}

@Composable
fun DocArticleScreen(docId: String) {
    val spacing = skTheme.tokens.spacing
    val catalogId = CatalogId.entries.firstOrNull { it.route == docId }
    val article = catalogId?.let { PlaygroundDocs.find(it) }
    val entry = catalogId?.let { PlaygroundCatalog.find(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle(article?.title ?: entry?.title ?: "Documentation")
        Text(
            text = article?.body ?: entry?.description ?: "Article not found: $docId",
            color = skTheme.tokens.colors.onSurface.toColor(),
        )
    }
}
