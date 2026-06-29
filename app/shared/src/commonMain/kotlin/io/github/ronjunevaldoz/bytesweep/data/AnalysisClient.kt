package io.github.ronjunevaldoz.bytesweep.data

import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeRequest
import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeResponse
import io.github.ronjunevaldoz.bytesweep.analysis.ScanItemDto
import io.github.ronjunevaldoz.bytesweep.domain.JunkAnalysisService
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Talks to the Bytesweep server's `/analyze` endpoint. The engine is selected
 * automatically per platform (OkHttp/CIO/Darwin/JS) from the classpath.
 *
 * [baseUrl] points at the Bytesweep server, not Ollama — the server proxies the LLM call.
 * Default targets a locally running server; override per platform (e.g. `10.0.2.2` on the
 * Android emulator).
 */
class AnalysisClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
) : JunkAnalysisService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun analyze(items: List<JunkItem>): AnalyzeResponse {
        val request = AnalyzeRequest(
            items = items.map {
                ScanItemDto(
                    id = it.id,
                    name = it.name,
                    path = it.path,
                    sizeBytes = it.sizeBytes,
                    category = it.category.name,
                )
            },
        )
        return client.post("${baseUrl.trimEnd('/')}/analyze") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://localhost:8080"
    }
}
