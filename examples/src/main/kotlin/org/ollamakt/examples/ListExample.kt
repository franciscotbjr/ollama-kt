package org.ollamakt.examples.list

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
            model.modifiedAt?.let { modified ->
                println("  Modified: $modified")
            }
            model.details?.let { details ->
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