package org.ollamakt.models

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.ollamakt.models.domain.Message
import org.ollamakt.models.domain.MessageRole
import org.ollamakt.models.requests.ChatRequest
import org.ollamakt.models.requests.GenerateRequest
import org.ollamakt.models.responses.ChatResponse

class ModelSerializationTest {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }
    
    @Test
    fun `should serialize and deserialize ChatRequest`() {
        val request = ChatRequest(
            model = "llama3.2",
            messages = listOf(
                Message(role = MessageRole.USER, content = "Hello")
            ),
            stream = false
        )
        
        val serialized = json.encodeToString(ChatRequest.serializer(), request)
        val deserialized = json.decodeFromString(ChatRequest.serializer(), serialized)
        
        assert(request.model == deserialized.model)
        assert(request.messages.size == deserialized.messages.size)
        assert(request.stream == deserialized.stream)
    }
    
    @Test
    fun `should serialize and deserialize GenerateRequest`() {
        val request = GenerateRequest(
            model = "llama3.2",
            prompt = "Generate text",
            stream = false
        )
        
        val serialized = json.encodeToString(GenerateRequest.serializer(), request)
        val deserialized = json.decodeFromString(GenerateRequest.serializer(), serialized)
        
        assert(request.model == deserialized.model)
        assert(request.prompt == deserialized.prompt)
        assert(request.stream == deserialized.stream)
    }
    
    // TODO: Fix ChatResponse serialization tests
    // Currently disabled due to Duration serialization issues
}