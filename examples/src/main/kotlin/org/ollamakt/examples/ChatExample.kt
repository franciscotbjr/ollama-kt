package org.ollamakt.examples

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.domain.Message
import org.ollamakt.models.domain.MessageRole
import org.ollamakt.models.requests.ChatRequest

/**
 * Chat API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.chat(ChatRequest(
            model = "qwen3:0.6b",
            messages = listOf(
                Message(MessageRole.USER, "What is the capital of Brazil?")
            )
        ))
        
        println("Model: ${response.model}")
        println("Response: ${response.message.content}")
        println("Done: ${response.done}")
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}