package io.github.ronjunevaldoz.bytesweep

import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeRequest
import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeResponse
import io.github.ronjunevaldoz.bytesweep.analysis.JunkAnalyzer
import io.github.ronjunevaldoz.bytesweep.analysis.Recommendation
import io.github.ronjunevaldoz.bytesweep.analysis.Safety
import io.github.ronjunevaldoz.bytesweep.analysis.ScanItemDto
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyzeRouteTest {

    private val fakeAnalyzer = object : JunkAnalyzer {
        override suspend fun analyze(request: AnalyzeRequest) = AnalyzeResponse(
            recommendations = request.items.map { Recommendation(it.id, Safety.SAFE, "fake") },
            summary = "fake summary",
        )
    }

    @Test
    fun `analyze returns a recommendation per item`() = testApplication {
        application { module(fakeAnalyzer) }
        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/analyze") {
            contentType(ContentType.Application.Json)
            setBody(
                AnalyzeRequest(
                    items = listOf(ScanItemDto("a", "cache", "/a", 100, "CACHE")),
                ),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body: AnalyzeResponse = response.body()
        assertEquals(1, body.recommendations.size)
        assertEquals("a", body.recommendations.first().id)
        assertEquals(Safety.SAFE, body.recommendations.first().safety)
    }
}
