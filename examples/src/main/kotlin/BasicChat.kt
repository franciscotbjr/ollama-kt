import org.ollamakt.models.domain.*
import org.ollamakt.models.requests.ChatRequest

/**
 * Basic chat example showing simple conversation with Ollama.
 */
fun main() {
    // Create a simple chat request
    val chatRequest = ChatRequest(
        model = "llama3.1",
        messages = listOf(
            Message(
                role = MessageRole.USER,
                content = "Hello! How are you?"
            )
        )
    )
    
    println("Chat Request created:")
    println("Model: ${chatRequest.model}")
    println("Messages: ${chatRequest.messages.size}")
    println("First message: ${chatRequest.messages.first().content}")
    
    // Example of creating a system message
    val systemMessage = Message(
        role = MessageRole.SYSTEM,
        content = "You are a helpful assistant that always responds in a friendly manner."
    )
    
    val chatWithSystem = chatRequest.copy(
        messages = listOf(systemMessage) + chatRequest.messages
    )
    
    println("\nChat with system message:")
    println("Total messages: ${chatWithSystem.messages.size}")
    println("System message: ${chatWithSystem.messages.first().content}")
}