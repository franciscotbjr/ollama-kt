package org.ollamakt.utils

import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.ollamakt.exceptions.OllamaException
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

/**
 * Utility functions for HTTP operations and response handling.
 */
object HttpUtils {
    
    /**
     * Check if an HTTP status code indicates success.
     */
    fun HttpStatusCode.isSuccess(): Boolean = value in 200..299
    
    /**
     * Check if an HTTP status code indicates a client error.
     */
    fun HttpStatusCode.isClientError(): Boolean = value in 400..499
    
    /**
     * Check if an HTTP status code indicates a server error.
     */
    fun HttpStatusCode.isServerError(): Boolean = value in 500..599
    
    /**
     * Convert HTTP status code to appropriate OllamaException.
     */
    fun HttpStatusCode.toOllamaException(responseBody: String? = null): OllamaException {
        return when {
            isClientError() -> when (value) {
                404 -> OllamaException.ModelNotFoundException("Model not found")
                400 -> OllamaException.HttpException(
                    statusCode = value,
                    message = "Bad request: $responseBody",
                    responseBody = responseBody
                )
                401 -> OllamaException.HttpException(
                    statusCode = value,
                    message = "Unauthorized access",
                    responseBody = responseBody
                )
                else -> OllamaException.HttpException(
                    statusCode = value,
                    message = "Client error: $description",
                    responseBody = responseBody
                )
            }
            isServerError() -> OllamaException.HttpException(
                statusCode = value,
                message = "Server error: $description",
                responseBody = responseBody
            )
            else -> OllamaException.HttpException(
                statusCode = value,
                message = "HTTP error: $description",
                responseBody = responseBody
            )
        }
    }
    
    /**
     * Safely read response body as text.
     */
    suspend fun HttpResponse.safeBodyAsText(): String {
        return try {
            bodyAsText()
        } catch (e: Exception) {
            "Unable to read response body: ${e.message}"
        }
    }
    
    /**
     * Parse Server-Sent Events (SSE) stream from ByteReadChannel.
     * Used for streaming responses from Ollama API.
     */
    suspend fun ByteReadChannel.parseSSEStream(): Flow<String> = flow {
        val buffer = StringBuilder()
        
        while (!isClosedForRead) {
            val line = readUTF8Line() ?: break
            
            when {
                line.startsWith("data: ") -> {
                    val data = line.removePrefix("data: ").trim()
                    if (data.isNotEmpty() && data != "[DONE]") {
                        emit(data)
                    }
                }
                line.isEmpty() -> {
                    // Empty line indicates end of event
                    if (buffer.isNotEmpty()) {
                        emit(buffer.toString())
                        buffer.clear()
                    }
                }
                else -> {
                    // Other SSE fields (event, id, retry) - we can ignore for Ollama
                    continue
                }
            }
        }
        
        // Emit any remaining data
        if (buffer.isNotEmpty()) {
            emit(buffer.toString())
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Parse JSON Lines format from ByteReadChannel.
     * Each line contains a complete JSON object.
     */
    suspend inline fun <reified T : Any> ByteReadChannel.parseJsonLines(
        json: Json = Json.Default
    ): Flow<T> = flow {
        while (!isClosedForRead) {
            val line = readUTF8Line() ?: break
            
            if (line.isNotBlank()) {
                try {
                    val item = json.decodeFromString<T>(line)
                    emit(item)
                } catch (e: Exception) {
                    throw OllamaException.SerializationException(
                        message = "Failed to parse JSON line: $line",
                        cause = e
                    )
                }
            }
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Parse streaming response based on content type.
     */
    suspend inline fun <reified T : Any> HttpResponse.parseStreamingResponse(
        json: Json = Json.Default
    ): Flow<T> {
        val contentType = contentType()
        
        return when {
            contentType?.match(ContentType.Text.EventStream) == true -> {
                // Server-Sent Events format
                bodyAsChannel().parseSSEStream().parseJsonObjects<T>(json)
            }
            contentType?.match(ContentType.Application.Json) == true -> {
                // JSON Lines format (used by Ollama)
                bodyAsChannel().parseJsonLines<T>(json)
            }
            else -> {
                // Fallback to JSON Lines
                bodyAsChannel().parseJsonLines<T>(json)
            }
        }
    }
    
    /**
     * Parse JSON objects from a Flow of JSON strings.
     */
    inline fun <reified T : Any> Flow<String>.parseJsonObjects(
        json: Json
    ): Flow<T> = flow {
        collect { jsonString ->
            try {
                val item = json.decodeFromString<T>(jsonString)
                emit(item)
            } catch (e: Exception) {
                throw OllamaException.SerializationException(
                    message = "Failed to parse JSON: $jsonString",
                    cause = e
                )
            }
        }
    }
    
    /**
     * Create default JSON configuration for API communication.
     */
    fun createDefaultJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        prettyPrint = false
        coerceInputValues = true
    }
    
    /**
     * Validate URL format.
     */
    fun String.isValidUrl(): Boolean {
        return try {
            val url = Url(this)
            url.protocol.name in setOf("http", "https")
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Build API endpoint URL.
     */
    fun buildApiUrl(baseUrl: String, endpoint: String): String {
        val cleanBase = baseUrl.trimEnd('/')
        val cleanEndpoint = endpoint.trimStart('/')
        return "$cleanBase/api/$cleanEndpoint"
    }
    
    /**
     * Extract error message from response body.
     */
    fun extractErrorMessage(responseBody: String?): String {
        if (responseBody.isNullOrBlank()) return "Unknown error"
        
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val errorMap = json.decodeFromString<Map<String, String>>(responseBody)
            errorMap["error"] ?: errorMap["message"] ?: responseBody
        } catch (e: Exception) {
            responseBody
        }
    }
}