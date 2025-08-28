package org.ollamakt.examples.ps

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
                process.sizeVram?.let { vram ->
                    println("  VRAM: ${vram / (1024 * 1024)} MB")
                }
                process.expiresAt?.let { expires ->
                    println("  Expires at: $expires")
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