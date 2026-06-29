package io.github.ronjunevaldoz.bytesweep.domain

import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeResponse
import io.github.ronjunevaldoz.bytesweep.model.JunkItem

/** Sends scanned junk items to the Bytesweep server for LLM-powered safety analysis. */
interface JunkAnalysisService {
    suspend fun analyze(items: List<JunkItem>): AnalyzeResponse
}

/** Use case wrapper so the presenter depends on the domain, not the data client. */
class AnalyzeJunkUseCase(private val service: JunkAnalysisService) {
    suspend operator fun invoke(items: List<JunkItem>): AnalyzeResponse = service.analyze(items)
}
