package org.ollamakt.examples

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient

/**
 * List models API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.list()
        
        println("Available models (${response.models.size}):")
        response.models.forEach { model ->
            println("- ${model.name}")
            println("  Size: ${model.size / (1024 * 1024)} MB")
            val modifiedAt = model.modifiedAt
            if (modifiedAt != null) {
                println("  Modified: $modifiedAt")
            }
            val details = model.details
            if (details != null) {
                println("  Format: ${details.format}")
                println("  Family: ${details.family}")
            }
            println()
        }
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}