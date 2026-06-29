package io.github.ronjunevaldoz.bytesweep.presenter

import io.github.ronjunevaldoz.bytesweep.core.mvi.MviViewModel
import io.github.ronjunevaldoz.bytesweep.domain.AnalyzeJunkUseCase
import io.github.ronjunevaldoz.bytesweep.domain.CleanJunkUseCase
import io.github.ronjunevaldoz.bytesweep.domain.ScanStorageUseCase
import io.github.ronjunevaldoz.bytesweep.ui.util.formatSize

class ScannerViewModel(
    private val scanStorage: ScanStorageUseCase,
    private val cleanJunk: CleanJunkUseCase,
    private val analyzeJunk: AnalyzeJunkUseCase,
) : MviViewModel<ScannerContract.State, ScannerContract.Intent, ScannerContract.Effect>(
    initialState = ScannerContract.State(),
) {

    override suspend fun handleIntent(intent: ScannerContract.Intent) {
        when (intent) {
            ScannerContract.Intent.ScanClicked -> scan()
            ScannerContract.Intent.CleanClicked -> clean()
            ScannerContract.Intent.AnalyzeClicked -> analyze()
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
}
