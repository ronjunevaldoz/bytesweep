package io.github.ronjunevaldoz.bytesweep.analysis

import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeRequest
import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeResponse

/**
 * Server-side LLM analysis boundary. The route depends only on this interface, so the
 * backing model can be swapped (Ollama now, Claude later) without touching the route.
 */
interface JunkAnalyzer {
    suspend fun analyze(request: AnalyzeRequest): AnalyzeResponse
}
