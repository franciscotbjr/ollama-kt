package org.ollamakt.examples.httpclient

import kotlinx.coroutines.runBlocking
import org.ollamakt.client.OllamaClient
import org.ollamakt.client.OllamaClientConfig
import org.ollamakt.models.domain.Message
import org.ollamakt.models.domain.MessageRole
import org.ollamakt.models.requests.ChatRequest
import org.ollamakt.models.requests.GenerateRequest

/**
 * Example demonstrating the HTTP client functionality.
 * 
 * This example shows how to:
 * 1. Create and configure an OllamaClient
 * 2. Make basic API calls (chat, generate, list)
 * 3. Handle errors properly
 * 4. Clean up resources
 */
fun main() {
    println("🚀 Ollama-KT HTTP Client Example")
    println("================================")
    
    // Create client with custom configuration
    val clientConfig = OllamaClientConfig.forTesting()
        .withLogging(enabled = true, level = "INFO")
        .withHeader("X-Example", "ollama-kt-demo")
        .withTimeouts(
            connectionTimeout = 10000L,
            requestTimeout = 60000L,
            socketTimeout = 10000L
        )
    
    val client = OllamaClient(clientConfig)
    
    runBlocking {
        try {
            println("\n📋 Configuration Summary:")
            println("Base URL: ${clientConfig.baseUrl}")
            println("User Agent: ${clientConfig.userAgent}")
            println("Logging: ${if (clientConfig.loggingEnabled) "Enabled (${clientConfig.loggingLevel})" else "Disabled"}")
            println("Custom Headers: ${clientConfig.customHeaders}")
            
            println("\n🔍 Testing API Structure...")
            
            // Example 1: List Models (if server available)
            println("\n1️⃣ List Models API structure:")
            println("   Endpoint: GET /api/tags")
            println("   Method: client.list()")
            val listResponse = client.list();
            println("   Returns: ListResponse with model information")
            println(listResponse)
            
            // Example 2: Generate API structure
            println("\n2️⃣ Generate Text API structure:")
            println("   Endpoint: POST /api/generate")
            println("   Method: client.generate(request)")
            val generateRequest = GenerateRequest(
                model = "llama3.2",
                prompt = "Hello, world!",
                stream = false
            )
            println("   Request: $generateRequest")
            
            // Example 3: Chat API structure
            println("\n3️⃣ Chat API structure:")
            println("   Endpoint: POST /api/chat")
            println("   Method: client.chat(request)")
            val chatRequest = ChatRequest(
                model = "llama3.2",
                messages = listOf(
                    Message(MessageRole.USER, "Hello!")
                ),
                stream = false
            )
            println("   Request: $chatRequest")
            
            println("\n✅ HTTP Client Implementation Complete!")
            println("\nThe client includes:")
            println("• ✅ Ktor-based HTTP client with CIO engine")
            println("• ✅ Automatic JSON serialization/deserialization")
            println("• ✅ Configurable timeouts and retry logic")
            println("• ✅ Comprehensive error handling with typed exceptions")
            println("• ✅ Logging support with configurable levels")
            println("• ✅ Custom headers and authentication support")
            println("• ✅ Connection pooling and keep-alive")
            println("• ✅ Type-safe configuration with validation")
            
            println("\n🔄 Next Steps:")
            println("• Add streaming support with Flow")
            println("• Implement remaining API endpoints")
            println("• Add integration tests with real Ollama server")
            
        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
            println("💡 Note: This is expected if no Ollama server is running")
        } finally {
            client.close()
            println("\n🔒 Client resources cleaned up")
        }
    }
}