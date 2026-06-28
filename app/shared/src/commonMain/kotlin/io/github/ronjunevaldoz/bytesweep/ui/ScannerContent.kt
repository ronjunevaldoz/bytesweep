package io.github.ronjunevaldoz.bytesweep.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ronjunevaldoz.bytesweep.getPlatform
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.presenter.ScannerContract
import io.github.ronjunevaldoz.bytesweep.ui.theme.BytesweepTheme
import io.github.ronjunevaldoz.bytesweep.ui.util.formatSize

/** Pure, previewable screen body — no ViewModel dependency. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerContent(
    state: ScannerContract.State,
    snackbarHostState: SnackbarHostState,
    onIntent: (ScannerContract.Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val platformName = remember { getPlatform().name }
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Bytesweep") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Button(
                    onClick = { onIntent(ScannerContract.Intent.CleanClicked) },
                    enabled = state.canClean,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(
                        if (state.isCleaning) "Cleaning…"
                        else "Clean ${formatSize(state.selectedBytes)}",
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        ) {
            SummaryCard(state, platformName)

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onIntent(ScannerContract.Intent.ScanClicked) },
                enabled = !state.isScanning && !state.isCleaning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isScanning) "Scanning…" else "Scan storage")
            }

            if (state.isScanning) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let { error ->
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { onIntent(ScannerContract.Intent.ErrorDismissed) }) {
                        Text("Dismiss")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                state.items.isNotEmpty() -> ItemList(state, onIntent)
                state.hasScanned && !state.isScanning -> EmptyState()
                else -> Unit
            }
        }
    }
}

@Composable
private fun SummaryCard(state: ScannerContract.State, platformName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Reclaimable", style = MaterialTheme.typography.labelMedium)
            Text(
                text = formatSize(state.selectedBytes),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            val subtitle = state.lastReclaimedBytes?.let { "Last clean reclaimed ${formatSize(it)}" }
                ?: "${state.items.size} items • $platformName"
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ItemList(
    state: ScannerContract.State,
    onIntent: (ScannerContract.Intent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = state.allSelected,
            onCheckedChange = { onIntent(ScannerContract.Intent.SelectAllToggled(it)) },
        )
        Text("Select all", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(0.dp))
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(state.items, key = { it.id }) { item ->
            JunkRow(item = item, onToggle = { onIntent(ScannerContract.Intent.ItemToggled(item.id)) })
        }
    }
}

@Composable
private fun JunkRow(item: JunkItem, onToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = item.selected, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(item.category.label, style = MaterialTheme.typography.bodySmall)
            }
            Text(formatSize(item.sizeBytes), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Nothing to clean 🎉", style = MaterialTheme.typography.titleMedium)
    }
}

// ---- Previews ----

private val sampleItems = listOf(
    JunkItem("1", "Glide image cache", "/cache/glide", 48_300_000, JunkCategory.CACHE),
    JunkItem("2", "Temp downloads", "/tmp/dl", 12_500_000, JunkCategory.TEMP, selected = false),
    JunkItem("3", "app.log", "/logs/app.log", 2_100_000, JunkCategory.LOGS),
    JunkItem("4", "old-backup.zip", "/tmp/old-backup.zip", 256_000_000, JunkCategory.LARGE_FILE),
)

@Preview
@Composable
private fun ScannerContentResultsPreview() {
    BytesweepTheme {
        ScannerContent(
            state = ScannerContract.State(hasScanned = true, items = sampleItems),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ScannerContentIdlePreview() {
    BytesweepTheme {
        ScannerContent(
            state = ScannerContract.State(),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ScannerContentScanningPreview() {
    BytesweepTheme {
        ScannerContent(
            state = ScannerContract.State(isScanning = true),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ScannerContentEmptyPreview() {
    BytesweepTheme {
        ScannerContent(
            state = ScannerContract.State(hasScanned = true, items = emptyList()),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
        )
    }
}
