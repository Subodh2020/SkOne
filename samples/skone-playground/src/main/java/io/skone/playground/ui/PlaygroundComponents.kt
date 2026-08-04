package io.skone.playground.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toDp
import io.skone.playground.catalog.CatalogEntry
import io.skone.playground.catalog.CatalogKind

@Composable
fun PlaygroundSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = skTheme.tokens.colors.onBackground.toColor(),
    )
}

@Composable
fun PlaygroundBody(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = skTheme.tokens.colors.onSurfaceVariant.toColor(),
    )
}

@Composable
fun CatalogEntryCard(
    entry: CatalogEntry,
    onClick: () -> Unit,
) {
    val spacing = skTheme.tokens.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = skTheme.tokens.colors.surface.toColor(),
                shape = RoundedCornerShape(skTheme.tokens.radius.md.toDp()),
            )
            .clickable(onClick = onClick)
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.xxs.toDp()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                color = skTheme.tokens.colors.onSurface.toColor(),
            )
            Text(
                text = entry.kind.name,
                style = MaterialTheme.typography.labelSmall,
                color = skTheme.tokens.colors.primary.toColor(),
            )
        }
        Text(
            text = entry.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = skTheme.tokens.colors.onSurfaceVariant.toColor(),
        )
        Text(
            text = entry.description,
            style = MaterialTheme.typography.bodySmall,
            color = skTheme.tokens.colors.onSurfaceVariant.toColor(),
        )
    }
}

@Composable
fun KindFilterRow(
    selected: CatalogKind?,
    onSelected: (CatalogKind?) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelected(null) },
            label = { Text("All") },
        )
        CatalogKind.entries.forEach { kind ->
            FilterChip(
                selected = selected == kind,
                onClick = { onSelected(kind) },
                label = { Text(kind.name) },
            )
        }
    }
}

@Composable
fun EditorTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun EditorSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = skTheme.tokens.colors.onSurface.toColor())
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun CodeBlock(title: String, code: String) {
    val spacing = skTheme.tokens.spacing
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.xs.toDp()),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = skTheme.tokens.colors.primary.toColor(),
        )
        Text(
            text = code,
            style = MaterialTheme.typography.bodySmall,
            color = skTheme.tokens.colors.onSurface.toColor(),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    skTheme.tokens.colors.surfaceVariant.toColor(),
                    RoundedCornerShape(skTheme.tokens.radius.sm.toDp()),
                )
                .padding(spacing.sm.toDp()),
        )
    }
}
