package io.github.ronjunevaldoz.bytesweep.presenter

import io.github.ronjunevaldoz.bytesweep.core.mvi.MviViewModel
import io.github.ronjunevaldoz.bytesweep.domain.AnalyzeJunkUseCase
import io.github.ronjunevaldoz.bytesweep.domain.CleanJunkUseCase
import io.github.ronjunevaldoz.bytesweep.domain.FileLocationOpener
import io.github.ronjunevaldoz.bytesweep.domain.FolderScanner
import io.github.ronjunevaldoz.bytesweep.domain.ScanStorageUseCase
import io.github.ronjunevaldoz.bytesweep.ui.util.formatSize

class ScannerViewModel(
    private val scanStorage: ScanStorageUseCase,
    private val cleanJunk: CleanJunkUseCase,
    private val analyzeJunk: AnalyzeJunkUseCase,
    private val fileLocationOpener: FileLocationOpener,
    private val folderScanner: FolderScanner,
) : MviViewModel<ScannerContract.State, ScannerContract.Intent, ScannerContract.Effect>(
    initialState = ScannerContract.State(
        canOpenLocations = fileLocationOpener.isSupported,
        canPickFolder = folderScanner.isSupported,
    ),
) {

    override suspend fun handleIntent(intent: ScannerContract.Intent) {
        when (intent) {
            ScannerContract.Intent.ScanClicked -> scan()
            ScannerContract.Intent.PickFolderClicked -> pickFolder()
            ScannerContract.Intent.CleanClicked ->
                // Deleting real user-chosen files needs a confirmation; junk is deleted directly.
                if (state.value.scannedViaFolder) updateState { copy(pendingClean = true) } else clean()
            ScannerContract.Intent.ConfirmCleanClicked -> deleteFolderItems()
            ScannerContract.Intent.CancelCleanClicked -> updateState { copy(pendingClean = false) }
            ScannerContract.Intent.AnalyzeClicked -> analyze()
            is ScannerContract.Intent.OpenLocationClicked -> openLocation(intent.id)
            ScannerContract.Intent.ErrorDismissed -> updateState { copy(error = null) }

            is ScannerContract.Intent.ItemToggled -> updateState {
                copy(items = items.map { if (it.id == intent.id) it.copy(selected = !it.selected) else it })
            }

            is ScannerContract.Intent.SelectAllToggled -> updateState {
                copy(items = items.map { it.copy(selected = intent.selected) })
            }
        }
    }

    private suspend fun scan() {
        if (state.value.isScanning) return
        updateState {
            copy(
                isScanning = true,
                error = null,
                lastReclaimedBytes = null,
                recommendations = emptyMap(),
                analysisSummary = null,
                scannedViaFolder = false,
                pendingClean = false,
            )
        }
        runCatching { scanStorage() }
            .onSuccess { result ->
                updateState { copy(isScanning = false, hasScanned = true, items = result.items) }
                val msg = if (result.items.isEmpty()) {
                    "No junk found — you're all clean!"
                } else {
                    "Found ${formatSize(result.totalBytes)} across ${result.items.size} items"
                }
                sendEffect(ScannerContract.Effect.ShowMessage(msg))
            }
            .onFailure { e ->
                updateState { copy(isScanning = false, error = e.message ?: "Scan failed") }
            }
    }

    private suspend fun clean() {
        val current = state.value
        if (!current.canClean) return
        val toClean = current.selectedItems
        updateState { copy(isCleaning = true, error = null) }
        runCatching { cleanJunk(toClean) }
            .onSuccess { reclaimed ->
                val cleanedIds = toClean.map { it.id }.toSet()
                updateState {
                    copy(
                        isCleaning = false,
                        items = items.filterNot { it.id in cleanedIds },
                        lastReclaimedBytes = reclaimed,
                    )
                }
                sendEffect(ScannerContract.Effect.ShowMessage("Reclaimed ${formatSize(reclaimed)}"))
            }
            .onFailure { e ->
                updateState { copy(isCleaning = false, error = e.message ?: "Cleaning failed") }
            }
    }

    private suspend fun analyze() {
        val current = state.value
        if (!current.canAnalyze) return
        updateState { copy(isAnalyzing = true, error = null) }
        runCatching { analyzeJunk(current.items) }
            .onSuccess { response ->
                updateState {
                    copy(
                        isAnalyzing = false,
                        recommendations = response.recommendations.associateBy { it.id },
                        analysisSummary = response.summary,
                    )
                }
                sendEffect(ScannerContract.Effect.ShowMessage("AI analysis complete"))
            }
            .onFailure { e ->
                updateState { copy(isAnalyzing = false, error = e.message ?: "Analysis failed") }
            }
    }

    private suspend fun pickFolder() {
        if (state.value.isScanning) return
        updateState {
            copy(
                isScanning = true,
                error = null,
                lastReclaimedBytes = null,
                recommendations = emptyMap(),
                analysisSummary = null,
                pendingClean = false,
            )
        }
        runCatching { folderScanner.pickAndScan() }
            .onSuccess { result ->
                if (result == null) {
                    updateState { copy(isScanning = false) } // user cancelled the chooser
                } else {
                    updateState {
                        copy(
                            isScanning = false,
                            hasScanned = true,
                            scannedViaFolder = true,
                            items = result.items,
                        )
                    }
                    val msg = if (result.items.isEmpty()) {
                        "No files found in that folder"
                    } else {
                        "Found ${formatSize(result.totalBytes)} across ${result.items.size} items"
                    }
                    sendEffect(ScannerContract.Effect.ShowMessage(msg))
                }
            }
            .onFailure { e ->
                updateState { copy(isScanning = false, error = e.message ?: "Folder scan failed") }
            }
    }

    private suspend fun deleteFolderItems() {
        val toDelete = state.value.selectedItems
        if (toDelete.isEmpty()) {
            updateState { copy(pendingClean = false) }
            return
        }
        updateState { copy(isCleaning = true, pendingClean = false, error = null) }
        runCatching { folderScanner.delete(toDelete) }
            .onSuccess { reclaimed ->
                val ids = toDelete.map { it.id }.toSet()
                updateState {
                    copy(
                        isCleaning = false,
                        items = items.filterNot { it.id in ids },
                        lastReclaimedBytes = reclaimed,
                    )
                }
                sendEffect(ScannerContract.Effect.ShowMessage("Deleted ${formatSize(reclaimed)}"))
            }
            .onFailure { e ->
                updateState { copy(isCleaning = false, error = e.message ?: "Delete failed") }
            }
    }

    private suspend fun openLocation(id: String) {
        val item = state.value.items.find { it.id == id } ?: return
        val opened = runCatching { fileLocationOpener.open(item) }.getOrDefault(false)
        if (!opened) {
            sendEffect(ScannerContract.Effect.ShowMessage("Couldn't open the file location"))
        }
    }
}
