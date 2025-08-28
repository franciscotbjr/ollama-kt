package org.ollamakt.client

import org.ollamakt.config.LibConfig

/**
 * Configuration class for OllamaClient.
 * 
 * This class encapsulates all configuration options for the HTTP client,
 * providing type-safe access to settings and validation.
 */
data class OllamaClientConfig(
    val baseUrl: String,
    val connectionTimeout: Long,
    val requestTimeout: Long,
    val socketTimeout: Long,
    val userAgent: String,
    val maxRetries: Int,
    val retryDelay: Long,
    val loggingEnabled: Boolean,
    val loggingLevel: String,
    val customHeaders: Map<String, String> = emptyMap()
) {
    
    init {
        require(baseUrl.isNotBlank()) { "Base URL cannot be blank" }
        require(connectionTimeout > 0) { "Connection timeout must be positive" }
        require(requestTimeout > 0) { "Request timeout must be positive" }
        require(socketTimeout > 0) { "Socket timeout must be positive" }
        require(userAgent.isNotBlank()) { "User agent cannot be blank" }
        require(maxRetries >= 0) { "Max retries must be non-negative" }
        require(retryDelay >= 0) { "Retry delay must be non-negative" }
        require(loggingLevel in setOf("DEBUG", "INFO", "WARN", "ERROR")) {
            "Logging level must be one of: DEBUG, INFO, WARN, ERROR"
        }
    }
    
    companion object {
        /**
         * Create configuration from LibConfig (properties file).
         */
        fun fromLibConfig(): OllamaClientConfig {
            return OllamaClientConfig(
                baseUrl = LibConfig.baseUrl,
                connectionTimeout = LibConfig.connectionTimeout,
                requestTimeout = LibConfig.requestTimeout,
                socketTimeout = LibConfig.socketTimeout,
                userAgent = LibConfig.userAgent,
                maxRetries = LibConfig.maxRetries,
                retryDelay = LibConfig.retryDelay,
                loggingEnabled = LibConfig.loggingEnabled,
                loggingLevel = LibConfig.loggingLevel.uppercase()
            )
        }
        
        /**
         * Create configuration for testing with default values.
         */
        fun forTesting(
            baseUrl: String = "http://localhost:11434",
            connectionTimeout: Long = 5000,
            requestTimeout: Long = 30000,
            socketTimeout: Long = 5000,
            customHeaders: Map<String, String> = emptyMap()
        ): OllamaClientConfig {
            return OllamaClientConfig(
                baseUrl = baseUrl,
                connectionTimeout = connectionTimeout,
                requestTimeout = requestTimeout,
                socketTimeout = socketTimeout,
                userAgent = "ollama-kt-test/1.0.0",
                maxRetries = 0,
                retryDelay = 0,
                loggingEnabled = false,
                loggingLevel = "ERROR",
                customHeaders = customHeaders
            )
        }
    }
    
    
    /**
     * Add custom headers to the configuration.
     */
    fun withHeaders(headers: Map<String, String>): OllamaClientConfig {
        return copy(customHeaders = this.customHeaders + headers)
    }
    
    /**
     * Add a single custom header to the configuration.
     */
    fun withHeader(name: String, value: String): OllamaClientConfig {
        return copy(customHeaders = this.customHeaders + (name to value))
    }
    
    /**
     * Enable or disable logging.
     */
    fun withLogging(enabled: Boolean, level: String = this.loggingLevel): OllamaClientConfig {
        return copy(loggingEnabled = enabled, loggingLevel = level.uppercase())
    }
    
    /**
     * Configure timeouts.
     */
    fun withTimeouts(
        connectionTimeout: Long = this.connectionTimeout,
        requestTimeout: Long = this.requestTimeout,
        socketTimeout: Long = this.socketTimeout
    ): OllamaClientConfig {
        return copy(
            connectionTimeout = connectionTimeout,
            requestTimeout = requestTimeout,
            socketTimeout = socketTimeout
        )
    }
    
    /**
     * Configure retry behavior.
     */
    fun withRetry(maxRetries: Int, retryDelay: Long = this.retryDelay): OllamaClientConfig {
        return copy(maxRetries = maxRetries, retryDelay = retryDelay)
    }
}