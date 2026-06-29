package io.github.ronjunevaldoz.bytesweep.analysis

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * [JunkAnalyzer] backed by a self-hosted Ollama instance. Calls `/api/chat` with a JSON
 * schema in the `format` field so the model returns output that maps directly onto
 * [AnalyzeResponse].
 *
 * Configured via env vars: `OLLAMA_BASE_URL` (default points at the dev instance) and
 * `OLLAMA_MODEL` (the model tag to run).
 */
class OllamaAnalyzer(
    baseUrl: String = System.getenv("OLLAMA_BASE_URL") ?: DEFAULT_BASE_URL,
    private val model: String = System.getenv("OLLAMA_MODEL") ?: DEFAULT_MODEL,
    private val client: HttpClient = defaultClient(),
) : JunkAnalyzer {

    private val chatUrl = "${baseUrl.trimEnd('/')}/api/chat"

    override suspend fun analyze(request: AnalyzeRequest): AnalyzeResponse {
        val itemsJson = json.encodeToString(AnalyzeRequest.serializer(), request)
        val ollamaRequest = OllamaChatRequest(
            model = model,
            stream = false,
            format = responseSchema,
            messages = listOf(
                OllamaMessage(role = "system", content = SYSTEM_PROMPT),
                OllamaMessage(role = "user", content = "Scan results:\n$itemsJson"),
            ),
        )

        val response: OllamaChatResponse = client.post(chatUrl) {
            contentType(ContentType.Application.Json)
            setBody(ollamaRequest)
            timeout { requestTimeoutMillis = 120_000 }
        }.body()

        return json.decodeFromString(AnalyzeResponse.serializer(), response.message.content)
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://ron-local-home.duckdns.org/ollama"
        const val DEFAULT_MODEL = "qwen3:8b"

        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        private fun defaultClient() = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
            }
        }

        private val SYSTEM_PROMPT = """
            You are Bytesweep's storage-cleanup assistant. You are given a list of files and
            directories discovered by a junk scan. For each item, decide how safe it is to
            delete and give a short, concrete reason.

            Rules:
            - SAFE: caches, temp files, and logs that apps regenerate on demand.
            - CAUTION: large files or items whose purpose is unclear — deletable but worth a look.
            - KEEP: anything that looks like user data or could break an app if removed.

            Respond ONLY with JSON matching the schema. Reference each item by its exact id.
            Keep each reason under 120 characters. End with a one-sentence overall summary.
        """.trimIndent()

        /** JSON schema mirroring [AnalyzeResponse] for Ollama structured output. */
        private val responseSchema: JsonElement = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("recommendations") {
                    put("type", "array")
                    putJsonObject("items") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("id") { put("type", "string") }
                            putJsonObject("safety") {
                                put("type", "string")
                                putJsonArray("enum") { add("SAFE"); add("CAUTION"); add("KEEP") }
                            }
                            putJsonObject("reason") { put("type", "string") }
                        }
                        putJsonArray("required") { add("id"); add("safety"); add("reason") }
                    }
                }
                putJsonObject("summary") { put("type", "string") }
            }
            putJsonArray("required") { add("recommendations"); add("summary") }
        }
    }
}

@Serializable
private data class OllamaChatRequest(
    val model: String,
    val messages: List<OllamaMessage>,
    val stream: Boolean = false,
    val format: JsonElement? = null,
)

@Serializable
private data class OllamaMessage(val role: String, val content: String)

@Serializable
private data class OllamaChatResponse(val message: OllamaMessage)
