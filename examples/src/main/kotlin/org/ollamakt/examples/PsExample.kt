package org.ollamakt.examples

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient

/**
 * PS (running processes) API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.ps()
        
        println("Running Models (${response.models.size}):")
        if (response.models.isEmpty()) {
            println("No models currently running")
        } else {
            response.models.forEach { process ->
                println("- ${process.name}")
                println("  Size: ${process.size / (1024 * 1024)} MB")
                val sizeVram = process.sizeVram
                if (sizeVram != null) {
                    println("  VRAM: ${sizeVram / (1024 * 1024)} MB")
                }
                val expiresAt = process.expiresAt
                if (expiresAt != null) {
                    println("  Expires at: $expiresAt")
                }
                println()
            }
        }
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}