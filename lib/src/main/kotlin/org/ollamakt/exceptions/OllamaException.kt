package org.ollamakt.exceptions

/**
 * Base class for all exceptions that can occur in the Ollama client.
 * This class is used to represent various types of errors that can occur during the operation of the Ollama client.
 *
 * @param message Optional custom message for the exception.
 * @param cause Optional cause of the exception.
 */
sealed class OllamaException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Exception thrown when there is an error related to the Ollama client.
     * This is a base class for all exceptions that can occur in the Ollama client.
     *
     * @param message Optional custom message for the exception.
     * @param cause Optional cause of the exception.
     */
    data class HttpException(
        val statusCode: Int,
        val responseBody: String? = null,
        override val message: String? = "HTTP $statusCode",

        override val cause: Throwable? = null
    ) : OllamaException(message, cause)

    /**
     * Exception thrown when there is a network-related error.
     * This is typically used when the Ollama client cannot connect to the server or there are issues with the network.
     *
     * @param message Optional custom message for the exception.
     * @param cause Optional cause of the exception.
     */
    data class NetworkException(
        override val message: String? = "Network error",
        override val cause: Throwable? = null
    ) : OllamaException(message, cause)

    /**
     * Exception thrown when there is an error during serialization or deserialization.
     * This is typically used when converting data to/from JSON or other formats.
     *
     * @param message Optional custom message for the exception.
     * @param cause Optional cause of the exception.
     */
    data class SerializationException(
        override val message: String? = "Serialization error",
        override val cause: Throwable? = null
    ) : OllamaException(message, cause)

    /**
     * Exception thrown when there is a configuration error.
     * This is typically used when the Ollama client is not properly configured.
     *
     * @param message Optional custom message for the exception.
     * @param cause Optional cause of the exception.
     */
    data class ConfigurationException(
        override val message: String? = "Configuration error",
        override val cause: Throwable? = null
    ) : OllamaException(message, cause)

    /**
     * Exception thrown when a model is not found.
     * This is typically used when trying to access a model that does not exist or is not available.
     *
     * @param modelName The name of the model that was not found.
     * @param message Optional custom message for the exception.
     * @param cause Optional cause of the exception.
     */
    data class ModelNotFoundException(
        val modelName: String,
        override val message: String? = "Model '$modelName' not found",
        override val cause: Throwable? = null
    ) : OllamaException(message, cause)

    /**
     * Exception thrown when an operation is cancelled by the user or system.
     * This is typically used to indicate that a long-running operation was interrupted.
     */
    data class CancellationException(
        override val message: String? = "Operation cancelled",
        override val cause: Throwable? = null
    ) : OllamaException(message, cause)

}