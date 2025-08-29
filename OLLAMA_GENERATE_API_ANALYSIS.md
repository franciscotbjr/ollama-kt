# Ollama Generate API - Complete Analysis

## Overview

The Ollama Generate API is the foundational endpoint for text generation, providing a comprehensive interface for single-turn completion tasks. It represents Ollama's core capability: transforming prompts into generated text responses.

## Endpoint Details

### HTTP Method & Path
```
POST /api/generate
```

### Core Purpose
Generate text completions from a given prompt using a specified model, with support for multimodal inputs, structured outputs, and real-time streaming.

## Request Structure

### ollama-kt Implementation
```kotlin
@Serializable
data class GenerateRequest(
    val model: String,                    // Required: Model name
    val prompt: String,                   // Required: Input prompt
    val images: List<String>? = null,     // Optional: Base64-encoded images
    val stream: Boolean = false,          // Optional: Enable streaming
    val format: ResponseFormat? = null,   // Optional: JSON/structured format
    val keepAlive: Duration? = null,      // Optional: Model memory duration
    val options: ModelOptions? = null,    // Optional: Model parameters
    val system: String? = null,           // Optional: System prompt
    val template: String? = null,         // Optional: Template override
    val context: List<Int>? = null,       // Optional: Previous context tokens
    val raw: Boolean = false              // Optional: Raw mode (no templates)
)
```

### Parameter Breakdown

#### Core Parameters
- **`model`** *(Required)*: Specifies which model to use for generation
  - Format: `model:tag` (e.g., `"qwen3:0.6b"`, `"llama3.2"`)
  - Must be a locally available model

- **`prompt`** *(Required)*: The input text to generate a response for
  - Can be questions, incomplete sentences, or any text
  - Example: `"Write a haiku about programming"`

#### Input Enhancement Parameters
- **`images`** *(Optional)*: Array of base64-encoded images for multimodal models
  - Enables vision-language tasks
  - Only supported by multimodal models

- **`system`** *(Optional)*: System prompt to guide model behavior
  - Sets context/personality for the model
  - Example: `"You are a helpful coding assistant"`

- **`template`** *(Optional)*: Custom prompt template
  - Overrides default model template
  - Advanced use case for specific formatting needs

#### Context & Memory Parameters
- **`context`** *(Optional)*: Previous conversation context as token arrays
  - Maintains conversation state across requests
  - Returned from previous `GenerateResponse.context`

- **`keepAlive`** *(Optional)*: How long to keep model in memory
  - Default: 5 minutes
  - Format: Kotlin Duration (e.g., `5.minutes`)

#### Output Control Parameters
- **`format`** *(Optional)*: Structured output format
  - `ResponseFormat.JSON` for JSON responses
  - Custom JSON schemas supported
  - Ensures parseable output

- **`raw`** *(Optional)*: Bypass prompt templates
  - `true`: Send prompt directly to model
  - `false`: Apply model's default template (default)

#### Performance Parameters
- **`stream`** *(Optional)*: Enable response streaming
  - `false`: Return complete response (default in ollama-kt)
  - `true`: Stream partial responses as they're generated

- **`options`** *(Optional)*: Fine-tune model behavior
  - Temperature, top_k, top_p, etc.
  - Model-specific parameters

## Response Structure

### ollama-kt Implementation
```kotlin
@Serializable
data class GenerateResponse(
    val model: String,                        // Confirms model used
    val createdAt: Instant? = null,          // Timestamp
    val response: String,                     // Generated text
    val done: Boolean,                        // Generation complete flag
    val context: List<Int>? = null,          // Context tokens for next request
    val totalDuration: Duration? = null,      // Total request time
    val loadDuration: Duration? = null,       // Model loading time
    val promptEvalCount: Int? = null,         // Input tokens processed
    val promptEvalDuration: Duration? = null, // Input processing time
    val evalCount: Int? = null,               // Output tokens generated
    val evalDuration: Duration? = null        // Output generation time
)
```

### Response Fields Analysis

#### Core Response
- **`model`**: Echo of the model name used
- **`response`**: The actual generated text
- **`done`**: Always `true` for non-streaming requests

#### Context Management
- **`context`**: Token representation of the conversation state
  - Use this in subsequent requests to maintain context
  - Essential for multi-turn conversations

#### Performance Metrics
- **`totalDuration`**: End-to-end request time
- **`loadDuration`**: Time to load model into memory
- **`promptEvalDuration`**: Time to process input prompt
- **`evalDuration`**: Time to generate output

#### Token Statistics
- **`promptEvalCount`**: Number of input tokens
- **`evalCount`**: Number of generated tokens
- Useful for billing/quota tracking

## Usage Examples

### Basic Generation
```kotlin
val client = OllamaClient()
val response = client.generate(GenerateRequest(
    model = "qwen3:0.6b",
    prompt = "Write a haiku about programming"
))
println("Generated: ${response.response}")
```

### With System Prompt
```kotlin
val response = client.generate(GenerateRequest(
    model = "llama3.2",
    prompt = "Explain quantum computing",
    system = "You are a physics professor explaining to undergraduate students"
))
```

### Structured JSON Output
```kotlin
val response = client.generate(GenerateRequest(
    model = "llama3.2",
    prompt = "Generate a product description",
    format = ResponseFormat.JSON
))
```

### Context-Aware Conversation
```kotlin
// First request
val response1 = client.generate(GenerateRequest(
    model = "qwen3:0.6b",
    prompt = "My name is Alice"
))

// Second request using context
val response2 = client.generate(GenerateRequest(
    model = "qwen3:0.6b", 
    prompt = "What's my name?",
    context = response1.context
))
```

## Implementation Details in ollama-kt

### Client Method
```kotlin
/**
 * Generate a response from a model.
 */
suspend fun generate(request: GenerateRequest): GenerateResponse {
    return executeRequest("generate", request.copy(stream = false))
}
```

### Key Implementation Decisions
1. **Non-streaming by default**: `stream = false` for simpler API usage
2. **Coroutine-based**: Fully asynchronous using Kotlin coroutines
3. **Type-safe**: Strongly-typed request/response models
4. **Error handling**: HTTP errors mapped to `OllamaException`

## API Principles

### 1. **Stateless with Context Support**
- Each request is independent
- Context tokens enable stateful conversations
- No server-side session management

### 2. **Flexibility-First Design**
- Support for various input types (text, images)
- Multiple output formats (text, JSON)
- Extensive parameter customization

### 3. **Performance Transparency**
- Detailed timing metrics
- Token count information
- Resource usage visibility

### 4. **Streaming-Optional**
- Real-time response streaming available
- Buffered complete responses for simpler usage
- Client can choose based on UX needs

### 5. **Model Agnostic**
- Works with any locally available model
- Parameters adapt to model capabilities
- Consistent interface across model types

## Use Cases

### 1. **Text Completion**
```kotlin
// Code completion
GenerateRequest(
    model = "codellama",
    prompt = "def fibonacci(n):",
    system = "Complete this Python function"
)
```

### 2. **Question Answering**
```kotlin
// Knowledge queries
GenerateRequest(
    model = "llama3.2",
    prompt = "What are the benefits of renewable energy?"
)
```

### 3. **Creative Writing**
```kotlin
// Story generation
GenerateRequest(
    model = "mistral",
    prompt = "Write a short story about a robot learning to paint",
    options = ModelOptions(temperature = 0.8)
)
```

### 4. **Data Generation**
```kotlin
// Structured data
GenerateRequest(
    model = "llama3.2",
    prompt = "Generate a customer profile",
    format = ResponseFormat.JSON
)
```

### 5. **Multimodal Analysis**
```kotlin
// Image description
GenerateRequest(
    model = "llava",
    prompt = "Describe what you see in this image",
    images = listOf(base64Image)
)
```

## Performance Characteristics

### Timing Analysis
Based on the response metrics:
- **Load time**: First request is slower (model loading)
- **Prompt evaluation**: Scales with input length
- **Generation time**: Scales with output length and complexity

### Resource Management
- Models stay in memory for `keepAlive` duration
- Subsequent requests reuse loaded models
- Memory usage scales with model size

### Token Economics
- Input tokens: Prompt + system + context
- Output tokens: Generated response
- Context tokens: Accumulated conversation state

## Error Handling

### Common Error Scenarios
1. **Model not found**: Specified model isn't locally available
2. **Resource exhaustion**: Insufficient memory/compute
3. **Invalid parameters**: Malformed request data
4. **Network issues**: Connection problems

### ollama-kt Error Mapping
```kotlin
catch (e: Exception) {
    throw when (e) {
        is OllamaException -> e
        is HttpRequestTimeoutException -> OllamaException.NetworkException(...)
        else -> OllamaException.NetworkException(...)
    }
}
```

## Best Practices

### 1. **Context Management**
- Store `context` from responses for conversation continuity
- Be mindful of context length limits
- Clear context when starting new topics

### 2. **Performance Optimization**
- Reuse clients to benefit from connection pooling
- Use appropriate `keepAlive` values
- Monitor token counts for resource planning

### 3. **Error Recovery**
- Implement retry logic for transient failures
- Validate model availability before requests
- Handle timeout scenarios gracefully

### 4. **Resource Efficiency**
- Close clients when done: `client.close()`
- Use streaming for long responses
- Set reasonable timeout values

## Comparison with Other Endpoints

| Feature | Generate | Chat |
|---------|----------|------|
| Input Style | Single prompt | Message array |
| Use Case | Completion | Conversation |
| Context | Manual tokens | Automatic history |
| Complexity | Simple | Advanced |

The Generate API serves as Ollama's foundational text generation interface, balancing simplicity with comprehensive feature support for diverse AI application needs.

---

*Analysis based on ollama-kt v1.0 implementation and Ollama API documentation*  
*Date: 2025-08-29*