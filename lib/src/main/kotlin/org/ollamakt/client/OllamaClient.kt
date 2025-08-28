package org.ollamakt.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import org.ollamakt.config.LibConfig
import org.ollamakt.exceptions.OllamaException
import org.ollamakt.models.requests.*
import org.ollamakt.models.responses.*
import java.io.Closeable

/**
 * Main client for interacting with the Ollama API.
 * 
 * This client provides a Kotlin-idiomatic interface for all Ollama operations,
 * using coroutines for asynchronous operations and Flow for streaming responses.
 */
class OllamaClient(
    private val configuration: OllamaClientConfig = OllamaClientConfig.fromLibConfig()
) : Closeable {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }
    
    private val httpClient = HttpClient(CIO) {
        expectSuccess = false // Handle HTTP errors manually
        
        install(ContentNegotiation) {
            json(json)
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = configuration.requestTimeout
            connectTimeoutMillis = configuration.connectionTimeout
            socketTimeoutMillis = configuration.socketTimeout
        }
        
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = configuration.maxRetries)
            exponentialDelay()
        }
        
        if (configuration.loggingEnabled) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = when (configuration.loggingLevel.uppercase()) {
                    "DEBUG" -> LogLevel.ALL
                    "INFO" -> LogLevel.INFO
                    "WARN" -> LogLevel.HEADERS
                    "ERROR" -> LogLevel.NONE
                    else -> LogLevel.INFO
                }
            }
        }
        
        defaultRequest {
            url(configuration.baseUrl)
            header(HttpHeaders.UserAgent, configuration.userAgent)
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            
            // Add custom headers if any
            configuration.customHeaders.forEach { (name, value) ->
                header(name, value)
            }
        }
    }
    
    /**
     * Execute a non-streaming request to the Ollama API.
     */
    private suspend inline fun <reified T> executeRequest(
        endpoint: String,
        request: Any
    ): T {
        try {
            val response = httpClient.post("/api/$endpoint") {
                setBody(request)
            }
            
            return handleResponse(response)
        } catch (e: Exception) {
            throw mapToOllamaException(e)
        }
    }
    
    /**
     * Execute a GET request to the Ollama API.
     */
    private suspend inline fun <reified T> executeGetRequest(
        endpoint: String
    ): T {
        try {
            val response = httpClient.get("/api/$endpoint")
            
            return handleResponse(response)
        } catch (e: Exception) {
            throw mapToOllamaException(e)
        }
    }
    
    /**
     * Execute a streaming request to the Ollama API.
     */
    private fun <T> executeStreamingRequest(
        endpoint: String,
        request: Any
    ): Flow<T> {
        TODO("Streaming implementation will be added in next phase")
    }
    
    /**
     * Handle HTTP response and convert to expected type.
     */
    private suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
        if (!response.status.isSuccess()) {
            val errorBody = try {
                response.bodyAsText()
            } catch (e: Exception) {
                "Unable to read error response"
            }
            
            throw OllamaException.HttpException(
                statusCode = response.status.value,
                message = "HTTP ${response.status.value}: ${response.status.description}",
                responseBody = errorBody
            )
        }
        
        return try {
            json.decodeFromString<T>(response.bodyAsText())
        } catch (e: Exception) {
            throw OllamaException.SerializationException(
                message = "Failed to parse response: ${e.message}",
                cause = e
            )
        }
    }
    
    /**
     * Map various exceptions to appropriate OllamaException types.
     */
    private fun mapToOllamaException(exception: Exception): OllamaException {
        return when (exception) {
            is OllamaException -> exception
            is HttpRequestTimeoutException -> OllamaException.NetworkException(
                message = "Request timeout: ${exception.message}",
                cause = exception
            )
            else -> OllamaException.NetworkException(
                message = "Network error: ${exception.message}",
                cause = exception
            )
        }
    }
    
    // API Methods - Basic Operations
    
    /**
     * Generate a response from a model.
     */
    suspend fun generate(request: GenerateRequest): GenerateResponse {
        return executeRequest("generate", request.copy(stream = false))
    }
    
    /**
     * Chat with a model using message history.
     */
    suspend fun chat(request: ChatRequest): ChatResponse {
        return executeRequest("chat", request.copy(stream = false))
    }
    
    /**
     * List available models.
     */
    suspend fun list(): ListResponse {
        return executeGetRequest("tags")
    }
    
    /**
     * Show model information.
     */
    suspend fun show(request: ShowRequest): ShowResponse {
        return executeRequest("show", request)
    }
    
    /**
     * Create a new model.
     */
    suspend fun create(request: CreateRequest): CreateResponse {
        return executeRequest("create", request.copy(stream = false))
    }
    
    /**
     * Delete a model.
     */
    suspend fun delete(request: DeleteRequest): DeleteResponse {
        try {
            val response = httpClient.delete("/api/delete") {
                setBody(request)
            }
            
            // Handle empty response body for successful delete operations
            return if (response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                if (responseBody.trim().isEmpty()) {
                    DeleteResponse(success = true)
                } else {
                    handleResponse(response)
                }
            } else {
                handleResponse(response)
            }
        } catch (e: Exception) {
            throw OllamaException.NetworkException("Failed to delete model: ${e.message}", e)
        }
    }
    
    /**
     * Copy a model.
     */
    suspend fun copy(request: CopyRequest): CopyResponse {
        return executeRequest("copy", request)
    }
    
    /**
     * Pull a model from the registry.
     */
    suspend fun pull(request: PullRequest): PullResponse {
        return executeRequest("pull", request.copy(stream = false))
    }
    
    /**
     * Push a model to the registry.
     */
    suspend fun push(request: PushRequest): PushResponse {
        return executeRequest("push", request.copy(stream = false))
    }
    
    /**
     * Generate embeddings for text.
     */
    suspend fun embed(request: EmbedRequest): EmbedResponse {
        return executeRequest("embed", request)
    }
    
    /**
     * List running models.
     */
    suspend fun ps(): ProcessListResponse {
        return executeGetRequest("ps")
    }
    
    override fun close() {
        httpClient.close()
    }
}