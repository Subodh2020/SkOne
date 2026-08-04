package io.skone.playground.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.playground.catalog.PlaygroundCatalog
import io.skone.playground.theme.ThemeSwitcher
import io.skone.theme.SKThemeMode

@Composable
fun HomeScreen(
    themeMode: SKThemeMode,
    onThemeModeChange: (SKThemeMode) -> Unit,
    onOpenCatalog: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenDocs: () -> Unit,
    onOpenSamples: () -> Unit,
    onOpenWidget: (String) -> Unit,
) {
    val spacing = skTheme.tokens.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("SKOne Playground")
        PlaygroundBody(
            "Official developer showcase for every SKOne component. " +
                "Browse the catalog, tweak live properties, copy Compose/XML, and switch themes.",
        )

        PlaygroundSectionTitle("Theme")
        ThemeSwitcher(mode = themeMode, onModeChange = onThemeModeChange)

        PlaygroundSectionTitle("Quick links")
        Button(onClick = onOpenCatalog, modifier = Modifier.fillMaxWidth()) {
            Text("Component catalog")
        }
        OutlinedButton(onClick = onOpenGallery, modifier = Modifier.fillMaxWidth()) {
            Text("Widget gallery")
        }
        OutlinedButton(onClick = onOpenSearch, modifier = Modifier.fillMaxWidth()) {
            Text("Search")
        }
        OutlinedButton(onClick = onOpenSamples, modifier = Modifier.fillMaxWidth()) {
            Text("Sample browser")
        }
        OutlinedButton(onClick = onOpenDocs, modifier = Modifier.fillMaxWidth()) {
            Text("Documentation")
        }

        PlaygroundSectionTitle("Widgets")
        PlaygroundCatalog.widgets().forEach { entry ->
            CatalogEntryCard(entry = entry, onClick = { onOpenWidget(entry.id.route) })
        }
    }
}
