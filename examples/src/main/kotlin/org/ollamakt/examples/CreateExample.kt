package org.ollamakt.examples

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.models.domain.Message
import org.ollamakt.models.domain.MessageRole
import org.ollamakt.models.requests.ChatRequest
import org.ollamakt.models.requests.CreateRequest

/**
 * Create model API example with actual calls.
 */
fun main() = runBlocking {
    val client = OllamaClient()
    
    try {
        println("Creating model 'my-assistant' from 'qwen3:0.6b'...")
        println("This may take a few moments...")
        
        val response = client.create(CreateRequest(
            model = "my-assistant",
            from = "qwen3:0.6b",
            system = "You are a helpful assistant that always responds in Portuguese."
        ))
        
        println("Create Response:")
        println("Status: ${response.status}")
        
        if (response.status == "success") {
            println("✅ Model 'my-assistant' created successfully!")
            
            // Verify the model was created by listing models
            val listResponse = client.list()
            val modelExists = listResponse.models.any { it.name.startsWith("my-assistant") }
            println("Model verification: ${if (modelExists) "✅ Found in model list" else "❌ Not found in model list"}")
            
            if (modelExists) {
                val createdModel = listResponse.models.find { it.name.startsWith("my-assistant") }
                println("Created model details:")
                println("  Name: ${createdModel?.name}")
                val modelSize = createdModel?.size
                println("  Size: ${if (modelSize != null) "${modelSize / (1024 * 1024)} MB" else "Unknown"}")
                println("  Family: ${createdModel?.details?.family}")
                
                // Test the created model with a user request
                println("\n--- Testing the created model ---")
                println("Sending test message to my-assistant...")
                
                val testResponse = client.chat(ChatRequest(
                    model = "my-assistant",
                    messages = listOf(
                        Message(MessageRole.USER, "Hello! Can you tell me about yourself?")
                    )
                ))
                
                println("Response from my-assistant:")
                println("${testResponse.message.content}")
                println("\nModel test completed successfully! ✅")
            }
        } else {
            println("❌ Model creation failed with status: ${response.status}")
        }
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        println("Note: This requires qwen3:0.6b to be available locally")
    } finally {
        client.close()
    }
}