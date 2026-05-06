package com.synapse.social.studioasinc.shared.data.repository.ai

import com.synapse.social.studioasinc.shared.core.config.SynapseConfig
import com.synapse.social.studioasinc.shared.domain.repository.ai.AiRepository
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GeminiAiRepository(
    private val httpClient: HttpClient
) : AiRepository {

    private val apiKey = SynapseConfig.GEMINI_API_KEY
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    override suspend fun generateSmartReplies(recentMessages: List<String>): Result<List<String>> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Gemini API Key is not configured"))
        }

        val prompt = """
            Based on the following recent chat messages, suggest 3 short, context-aware smart replies.
            Provide only the replies, each on a new line, without any numbering or extra text.

            Recent messages:
            ${recentMessages.joinToString("\n")}
        """.trimIndent()

        return try {
            val response: GeminiResponse = httpClient.post("$baseUrl?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt))))))
            }.body()

            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Result.failure(Exception("Empty response from Gemini"))

            val replies = text.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .take(3)

            Result.success(replies)
        } catch (e: Exception) {
            Napier.e("Error generating smart replies from Gemini", e)
            Result.failure(e)
        }
    }

    @Serializable
    private data class GeminiRequest(
        val contents: List<Content>
    )

    @Serializable
    private data class Content(
        val parts: List<Part>
    )

    @Serializable
    private data class Part(
        val text: String
    )

    @Serializable
    private data class GeminiResponse(
        val candidates: List<Candidate>
    )

    @Serializable
    private data class Candidate(
        val content: Content
    )
}
