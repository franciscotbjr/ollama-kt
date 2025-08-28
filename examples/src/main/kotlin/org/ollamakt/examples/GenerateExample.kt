package org.ollamakt.examples.generate

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.requests.GenerateRequest

/**
 * Generate API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        println("Generating text with qwen3:0.6b...")
        val response = client.generate(GenerateRequest(
            model = "qwen3:0.6b",
            prompt = "Write a haiku about programming"
        ))
        
        println("Model: ${response.model}")
        println("Generated text: ${response.response}")
        println("Done: ${response.done}")
        response.context?.let { context ->
            if (context.isNotEmpty()) {
                println("Context length: ${context.size}")
            }
        }
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}