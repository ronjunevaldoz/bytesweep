package io.github.ronjunevaldoz.bytesweep.analysis

import kotlinx.serialization.Serializable

/**
 * Shared request/response contract for the LLM-powered scan analysis, used by both
 * the Ktor server (`:server`) and the KMP client (`:app:shared`).
 */

@Serializable
data class ScanItemDto(
    val id: String,
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val category: String,
)

@Serializable
data class AnalyzeRequest(
    val items: List<ScanItemDto>,
)

/** How safe an item is to delete, as judged by the model. */
@Serializable
enum class Safety { SAFE, CAUTION, KEEP }

@Serializable
data class Recommendation(
    val id: String,
    val safety: Safety,
    val reason: String,
)

@Serializable
data class AnalyzeResponse(
    val recommendations: List<Recommendation>,
    val summary: String,
)
