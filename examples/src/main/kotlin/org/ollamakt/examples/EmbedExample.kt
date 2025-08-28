package org.ollamakt.examples.embed

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.requests.EmbedRequest

/**
 * Embed API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.embed(EmbedRequest(
            model = "mxbai-embed-large",
            prompt = "Hello, how are you today?"
        ))
        
        println("Embedding Response:")
        println("Vector dimension: ${response.embedding.size}")
        println("First 5 values: ${response.embedding.take(5)}")
        println("Last 5 values: ${response.embedding.takeLast(5)}")
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        println("Note: This requires mxbai-embed-large model")
        println("Run: ollama pull mxbai-embed-large")
    } finally {
        client.close()
    }
}