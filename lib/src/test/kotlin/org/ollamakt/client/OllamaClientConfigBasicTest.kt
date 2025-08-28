package org.ollamakt.client

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OllamaClientConfigBasicTest {
    
    @Test
    fun `should create config from LibConfig defaults`() {
        val config = OllamaClientConfig.fromLibConfig()
        
        // Basic validation that config is created
        assert(config.baseUrl.isNotBlank())
        assert(config.userAgent.isNotBlank())
        assert(config.connectionTimeout > 0)
        assert(config.requestTimeout > 0)
    }
    
    @Test
    fun `should create config for testing`() {
        val config = OllamaClientConfig.forTesting()
        
        // Basic validation for test config
        assert(config.baseUrl == "http://localhost:11434")
        assert(config.userAgent.contains("test"))
        assert(config.maxRetries == 0) // No retries in testing
        assert(!config.loggingEnabled) // No logging in tests
    }
    
    @Test
    fun `should validate configuration parameters`() {
        assertThrows<IllegalArgumentException> {
            OllamaClientConfig(
                baseUrl = "", // Invalid: blank URL
                connectionTimeout = 5000L,
                requestTimeout = 30000L,
                socketTimeout = 5000L,
                userAgent = "test",
                maxRetries = 0,
                retryDelay = 0L,
                loggingEnabled = false,
                loggingLevel = "INFO"
            )
        }
        
        assertThrows<IllegalArgumentException> {
            OllamaClientConfig(
                baseUrl = "http://localhost:11434",
                connectionTimeout = -1L, // Invalid: negative timeout
                requestTimeout = 30000L,
                socketTimeout = 5000L,
                userAgent = "test",
                maxRetries = 0,
                retryDelay = 0L,
                loggingEnabled = false,
                loggingLevel = "INFO"
            )
        }
    }
    
    @Test
    fun `should add custom headers`() {
        val config = OllamaClientConfig.forTesting()
        val withHeaders = config.withHeaders(mapOf(
            "X-Custom-Header" to "value1",
            "Authorization" to "Bearer token"
        ))
        
        assert(withHeaders.customHeaders.size == 2)
        assert(withHeaders.customHeaders["X-Custom-Header"] == "value1")
        assert(withHeaders.customHeaders["Authorization"] == "Bearer token")
    }
}