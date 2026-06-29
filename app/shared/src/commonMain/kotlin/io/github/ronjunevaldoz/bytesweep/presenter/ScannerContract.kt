package io.github.ronjunevaldoz.bytesweep.presenter

import io.github.ronjunevaldoz.bytesweep.analysis.Recommendation
import io.github.ronjunevaldoz.bytesweep.model.JunkItem

object ScannerContract {

    data class State(
        val isScanning: Boolean = false,
        val isCleaning: Boolean = false,
        val isAnalyzing: Boolean = false,
        val hasScanned: Boolean = false,
        val items: List<JunkItem> = emptyList(),
        val recommendations: Map<String, Recommendation> = emptyMap(),
        val analysisSummary: String? = null,
        val lastReclaimedBytes: Long? = null,
        val error: String? = null,
    ) {
        val selectedItems: List<JunkItem> get() = items.filter { it.selected }
        val selectedBytes: Long get() = selectedItems.sumOf { it.sizeBytes }
        val totalBytes: Long get() = items.sumOf { it.sizeBytes }
        val allSelected: Boolean get() = items.isNotEmpty() && items.all { it.selected }
        val canClean: Boolean get() = !isScanning && !isCleaning && selectedItems.isNotEmpty()
        val canAnalyze: Boolean get() = !isScanning && !isCleaning && !isAnalyzing && items.isNotEmpty()
    }

    sealed interface Intent {
        data object ScanClicked : Intent
        data class ItemToggled(val id: String) : Intent
        data class SelectAllToggled(val selected: Boolean) : Intent
        data object CleanClicked : Intent
        data object AnalyzeClicked : Intent
        data object ErrorDismissed : Intent
    }

    sealed interface Effect {
        data class ShowMessage(val message: String) : Effect
    }
}
