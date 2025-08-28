package org.ollamakt.examples.delete

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.requests.DeleteRequest

/**
 * Delete model API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        val modelName = "my-assistant"
        println("Checking if model '$modelName' exists...")
        
        // First, check if the model exists
        val listResponse = client.list()
        val modelExists = listResponse.models.any { it.name.startsWith(modelName) }
        
        if (modelExists) {
            val modelToDelete = listResponse.models.find { it.name.startsWith(modelName) }
            println("Found model: ${modelToDelete?.name}")
            println("Deleting model...")
            
            val response = client.delete(DeleteRequest(
                model = modelName
            ))
            
            println("Delete Response:")
            println("Success: ${response.success}")
            
            // Verify the model was deleted
            val updatedListResponse = client.list()
            val stillExists = updatedListResponse.models.any { it.name.startsWith(modelName) }
            println("Verification: Model deleted successfully = ${!stillExists}")
            
        } else {
            println("Model '$modelName' not found.")
            println("Available models:")
            listResponse.models.forEach { model ->
                println("  - ${model.name}")
            }
            println("\nNote: Run CreateExample first to create the model, then try deleting it.")
        }
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        println("Note: This deletes the model created by CreateExample")
    } finally {
        client.close()
    }
}