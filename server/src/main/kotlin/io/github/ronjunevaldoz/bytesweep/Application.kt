package io.github.ronjunevaldoz.bytesweep

import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeRequest
import io.github.ronjunevaldoz.bytesweep.analysis.JunkAnalyzer
import io.github.ronjunevaldoz.bytesweep.analysis.OllamaAnalyzer
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = { module() })
        .start(wait = true)
}

fun Application.module(analyzer: JunkAnalyzer = OllamaAnalyzer()) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    routing {
        get("/") {
            call.respondText(sayHello("Ktor"))
        }
        post("/analyze") {
            val request = call.receive<AnalyzeRequest>()
            runCatching { analyzer.analyze(request) }
                .onSuccess { call.respond(it) }
                .onFailure {
                    call.respond(
                        HttpStatusCode.BadGateway,
                        mapOf("error" to (it.message ?: "analysis failed")),
                    )
                }
        }
    }
}
