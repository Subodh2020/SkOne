package io.skone.playground.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.playground.catalog.CatalogKind
import io.skone.playground.catalog.PlaygroundCatalog

@Composable
fun CatalogScreen(
    onOpenEntry: (String, CatalogKind) -> Unit,
) {
    val spacing = skTheme.tokens.spacing
    var query by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf<CatalogKind?>(null) }
    val results = remember(query, kind) {
        PlaygroundCatalog.search(query).filter { kind == null || it.kind == kind }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.sm.toDp()),
    ) {
        PlaygroundSectionTitle("Component catalog")
        PlaygroundBody("Every future SKOne component registers here.")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Filter catalog") },
            singleLine = true,
        )
        KindFilterRow(selected = kind, onSelected = { kind = it })
        results.forEach { entry ->
            CatalogEntryCard(
                entry = entry,
                onClick = { onOpenEntry(entry.id.route, entry.kind) },
            )
        }
    }
}

@Composable
fun SearchScreen(
    onOpenEntry: (String, CatalogKind) -> Unit,
) {
    val spacing = skTheme.tokens.spacing
    var query by remember { mutableStateOf("") }
    val results = remember(query) { PlaygroundCatalog.search(query) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.sm.toDp()),
    ) {
        PlaygroundSectionTitle("Search")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search widgets, docs, samples…") },
            singleLine = true,
        )
        if (query.isBlank()) {
            PlaygroundBody("Try “textfield”, “form”, “theme”, or “xml”.")
        }
        results.forEach { entry ->
            CatalogEntryCard(
                entry = entry,
                onClick = { onOpenEntry(entry.id.route, entry.kind) },
            )
        }
    }
}
