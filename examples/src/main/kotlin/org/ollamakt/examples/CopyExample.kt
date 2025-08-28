package org.ollamakt.examples.copy

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.requests.CopyRequest

/**
 * Copy model API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.copy(CopyRequest(
            source = "qwen3:0.6b",
            destination = "qwen3-backup"
        ))
        
        println("Copy Response:")
        println("Success: ${response.success}")
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        println("Note: This requires qwen3:0.6b to be available locally")
    } finally {
        client.close()
    }
}