package org.ollamakt.examples.show

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.requests.ShowRequest

/**
 * Show model info API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val response = client.show(ShowRequest(
            model = "qwen3:0.6b"
        ))
        
        println("Model Information:")
        println("License: ${response.license}")
        println("System: ${response.system}")
        println("Template: ${response.template}")
        
        response.details?.let { details ->
            println("\nModel Details:")
            println("Format: ${details.format}")
            println("Family: ${details.family}")
            println("Parameter Size: ${details.parameterSize}")
            println("Quantization: ${details.quantizationLevel}")
        }
        
        response.modelfile?.let { modelfile ->
            println("\nModelfile (first 200 chars):")
            println(modelfile.take(200) + if (modelfile.length > 200) "..." else "")
        }
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}