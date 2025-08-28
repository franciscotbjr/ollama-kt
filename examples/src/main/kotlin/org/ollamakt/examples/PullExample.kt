package org.ollamakt.examples.pull

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.requests.PullRequest

/**
 * Pull model API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.pull(PullRequest(
            model = "phi3:mini",
            insecure = false
        ))
        
        println("Pull Response:")
        println("Status: ${response.status}")
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        println("Note: This example pulls a small model (phi3:mini)")
        println("Make sure you have internet connection and disk space")
    } finally {
        client.close()
    }
}