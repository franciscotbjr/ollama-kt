package org.ollamakt.exceptions

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class OllamaExceptionTest {

    @Nested
    @DisplayName("HttpException Tests")
    inner class HttpExceptionTests {

        @Test
        @DisplayName("Should create HttpException with status code only")
        fun shouldCreateHttpExceptionWithStatusCodeOnly() {
            val statusCode = 404
            val exception = OllamaException.HttpException(statusCode)

            assertEquals(statusCode, exception.statusCode)
            assertEquals("HTTP 404", exception.message)
            assertNull(exception.responseBody)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should create HttpException with all parameters")
        fun shouldCreateHttpExceptionWithAllParameters() {
            val statusCode = 500
            val responseBody = "Internal Server Error"
            val message = "Server error occurred"
            val cause = RuntimeException("Root cause")

            val exception = OllamaException.HttpException(
                statusCode = statusCode,
                responseBody = responseBody,
                message = message,
                cause = cause
            )

            assertEquals(statusCode, exception.statusCode)
            assertEquals(responseBody, exception.responseBody)
            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
        }

        @Test
        @DisplayName("Should be instance of OllamaException")
        fun shouldBeInstanceOfOllamaException() {
            val exception = OllamaException.HttpException(400)
            assertTrue(exception is OllamaException)
        }
    }

    @Nested
    @DisplayName("NetworkException Tests")
    inner class NetworkExceptionTests {

        @Test
        @DisplayName("Should create NetworkException with default message")
        fun shouldCreateNetworkExceptionWithDefaultMessage() {
            val exception = OllamaException.NetworkException()

            assertEquals("Network error", exception.message)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should create NetworkException with custom message and cause")
        fun shouldCreateNetworkExceptionWithCustomMessageAndCause() {
            val message = "Connection timeout"
            val cause = java.net.SocketTimeoutException("Timeout")

            val exception = OllamaException.NetworkException(message, cause)

            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
        }

        @Test
        @DisplayName("Should be instance of OllamaException")
        fun shouldBeInstanceOfOllamaException() {
            val exception = OllamaException.NetworkException()
            assertTrue(exception is OllamaException)
        }
    }

    @Nested
    @DisplayName("SerializationException Tests")
    inner class SerializationExceptionTests {

        @Test
        @DisplayName("Should create SerializationException with default message")
        fun shouldCreateSerializationExceptionWithDefaultMessage() {
            val exception = OllamaException.SerializationException()

            assertEquals("Serialization error", exception.message)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should create SerializationException with custom message and cause")
        fun shouldCreateSerializationExceptionWithCustomMessageAndCause() {
            val message = "JSON parsing failed"
            val cause = IllegalArgumentException("Invalid JSON format")

            val exception = OllamaException.SerializationException(message, cause)

            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
        }

        @Test
        @DisplayName("Should be instance of OllamaException")
        fun shouldBeInstanceOfOllamaException() {
            val exception = OllamaException.SerializationException()
            assertTrue(exception is OllamaException)
        }
    }

    @Nested
    @DisplayName("ConfigurationException Tests")
    inner class ConfigurationExceptionTests {

        @Test
        @DisplayName("Should create ConfigurationException with default message")
        fun shouldCreateConfigurationExceptionWithDefaultMessage() {
            val exception = OllamaException.ConfigurationException()

            assertEquals("Configuration error", exception.message)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should create ConfigurationException with custom message and cause")
        fun shouldCreateConfigurationExceptionWithCustomMessageAndCause() {
            val message = "Invalid API key"
            val cause = IllegalArgumentException("API key cannot be null")

            val exception = OllamaException.ConfigurationException(message, cause)

            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
        }

        @Test
        @DisplayName("Should be instance of OllamaException")
        fun shouldBeInstanceOfOllamaException() {
            val exception = OllamaException.ConfigurationException()
            assertTrue(exception is OllamaException)
        }
    }

    @Nested
    @DisplayName("ModelNotFoundException Tests")
    inner class ModelNotFoundExceptionTests {

        @Test
        @DisplayName("Should create ModelNotFoundException with model name only")
        fun shouldCreateModelNotFoundExceptionWithModelNameOnly() {
            val modelName = "llama2"
            val exception = OllamaException.ModelNotFoundException(modelName)

            assertEquals(modelName, exception.modelName)
            assertEquals("Model 'llama2' not found", exception.message)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should create ModelNotFoundException with all parameters")
        fun shouldCreateModelNotFoundExceptionWithAllParameters() {
            val modelName = "codellama"
            val message = "Model not available in this region"
            val cause = RuntimeException("Region restriction")

            val exception = OllamaException.ModelNotFoundException(
                modelName = modelName,
                message = message,
                cause = cause
            )

            assertEquals(modelName, exception.modelName)
            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
        }

        @Test
        @DisplayName("Should be instance of OllamaException")
        fun shouldBeInstanceOfOllamaException() {
            val exception = OllamaException.ModelNotFoundException("test-model")
            assertTrue(exception is OllamaException)
        }
    }

    @Nested
    @DisplayName("CancellationException Tests")
    inner class CancellationExceptionTests {

        @Test
        @DisplayName("Should create CancellationException with default message")
        fun shouldCreateCancellationExceptionWithDefaultMessage() {
            val exception = OllamaException.CancellationException()

            assertEquals("Operation cancelled", exception.message)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should create CancellationException with custom message and cause")
        fun shouldCreateCancellationExceptionWithCustomMessageAndCause() {
            val message = "User cancelled the operation"
            val cause = InterruptedException("Thread interrupted")

            val exception = OllamaException.CancellationException(message, cause)

            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
        }

        @Test
        @DisplayName("Should be instance of OllamaException")
        fun shouldBeInstanceOfOllamaException() {
            val exception = OllamaException.CancellationException()
            assertTrue(exception is OllamaException)
        }
    }

    @Nested
    @DisplayName("OllamaException Base Class Tests")
    inner class BaseClassTests {

        @Test
        @DisplayName("All exception types should inherit from Exception")
        fun allExceptionTypesShouldInheritFromException() {
            val httpException = OllamaException.HttpException(404)
            val networkException = OllamaException.NetworkException()
            val serializationException = OllamaException.SerializationException()
            val configurationException = OllamaException.ConfigurationException()
            val modelNotFoundException = OllamaException.ModelNotFoundException("test")
            val cancellationException = OllamaException.CancellationException()

            assertTrue(httpException is Exception)
            assertTrue(networkException is Exception)
            assertTrue(serializationException is Exception)
            assertTrue(configurationException is Exception)
            assertTrue(modelNotFoundException is Exception)
            assertTrue(cancellationException is Exception)
        }

        @Test
        @DisplayName("Exception messages should be accessible through Exception interface")
        fun exceptionMessagesShouldBeAccessibleThroughExceptionInterface() {
            val exceptions: List<Exception> = listOf(
                OllamaException.HttpException(404),
                OllamaException.NetworkException(),
                OllamaException.SerializationException(),
                OllamaException.ConfigurationException(),
                OllamaException.ModelNotFoundException("test"),
                OllamaException.CancellationException()
            )

            exceptions.forEach { exception ->
                assertNotNull(exception.message)
                assertTrue(exception.message!!.isNotEmpty())
            }
        }
    }
}