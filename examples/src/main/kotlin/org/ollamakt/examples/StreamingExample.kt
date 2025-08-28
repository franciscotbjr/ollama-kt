package org.ollamakt.examples.streaming

import org.ollamakt.models.domain.*
import org.ollamakt.models.requests.ChatRequest

/**
 * Streaming example showing how to handle streaming responses.
 */
fun main() {
    // Create a streaming chat request
    val streamingRequest = ChatRequest(
        model = "llama3.1",
        messages = listOf(
            Message(
                role = MessageRole.USER,
                content = "Tell me a story about dragons"
            )
        ),
        stream = true // Enable streaming
    )
    
    println("Streaming Chat Request created:")
    println("Model: ${streamingRequest.model}")
    println("Stream enabled: ${streamingRequest.stream}")
    println("Message: ${streamingRequest.messages.first().content}")
    
    // Example of a complete conversation with multiple messages
    val conversationMessages = listOf(
        Message(
            role = MessageRole.SYSTEM,
            content = "You are a creative storyteller."
        ),
        Message(
            role = MessageRole.USER,
            content = "Tell me a short story about a brave knight."
        ),
        Message(
            role = MessageRole.ASSISTANT,
            content = "Once upon a time, there was a brave knight named Sir Galahad..."
        ),
        Message(
            role = MessageRole.USER,
            content = "What happened next?"
        )
    )
    
    val conversationRequest = ChatRequest(
        model = "llama3.1",
        messages = conversationMessages,
        stream = true
    )
    
    println("\nConversation with ${conversationRequest.messages.size} messages:")
    conversationRequest.messages.forEachIndexed { index, message ->
        println("${index + 1}. ${message.role}: ${message.content.take(50)}...")
    }
}