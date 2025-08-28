package org.ollamakt.examples.chatnothink

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.domain.Message
import org.ollamakt.models.domain.MessageRole
import org.ollamakt.models.requests.ChatRequest

/**
 * Chat API example with thinking disabled (think = false).
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.chat(ChatRequest(
            model = "qwen3:0.6b",
            messages = listOf(
                Message(MessageRole.USER, "What is the capital of Brazil?")
            ),
            think = false
        ))
        
        println("Model: ${response.model}")
        println("Response: ${response.message.content}")
        println("Done: ${response.done}")
        println("Thinking disabled: No <think> tags should appear")
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}