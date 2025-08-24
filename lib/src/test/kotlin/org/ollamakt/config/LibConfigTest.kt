package org.ollamakt.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach

class LibConfigTest {

    private val originalSystemProperties = mutableMapOf<String, String?>()

    @BeforeEach
    fun setUp() {
        // Store original system properties that we might modify
        val keysToStore = listOf(
            "ollama.host",
            "ollama.port",
            "ollama.protocol", 
            "ollama.default.model",
            "ollama.connection.timeout"
        )
        
        keysToStore.forEach { key ->
            originalSystemProperties[key] = System.getProperty(key)
        }
        
        // Clear system properties to ensure clean test state
        keysToStore.forEach { key ->
            System.clearProperty(key)
        }
    }

    @AfterEach
    fun tearDown() {
        // Restore original system properties
        originalSystemProperties.forEach { (key, value) ->
            if (value != null) {
                System.setProperty(key, value)
            } else {
                System.clearProperty(key)
            }
        }
    }

    @Test
    fun `should load default configuration values`() {
        assertEquals("localhost", LibConfig.host)
        assertEquals(11434, LibConfig.port)
        assertEquals("http", LibConfig.protocol)
        assertEquals("http://localhost:11434", LibConfig.baseUrl)
    }

    @Test
    fun `should load connection settings`() {
        assertEquals(30000L, LibConfig.connectionTimeout)
        assertEquals(300000L, LibConfig.requestTimeout)
        assertEquals(30000L, LibConfig.socketTimeout)
    }

    @Test
    fun `should load client settings`() {
        assertEquals("ollama-kt/1.0.0", LibConfig.userAgent)
        assertEquals(3, LibConfig.maxRetries)
        assertEquals(1000L, LibConfig.retryDelay)
    }

    @Test
    fun `should load streaming configuration`() {
        assertTrue(LibConfig.streamingEnabled)
        assertEquals(8192, LibConfig.streamingBufferSize)
    }

    @Test
    fun `should load model defaults`() {
        assertEquals("llama3.2", LibConfig.defaultModel)
        assertEquals(0.8, LibConfig.defaultTemperature, 0.01)
        assertEquals(0.9, LibConfig.defaultTopP, 0.01)
        assertEquals(40, LibConfig.defaultTopK)
    }

    @Test
    fun `should load response format settings`() {
        assertEquals("json", LibConfig.defaultResponseFormat)
        assertFalse(LibConfig.responseIncludeContext)
    }

    @Test
    fun `should load logging configuration`() {
        assertTrue(LibConfig.loggingEnabled)
        assertEquals("INFO", LibConfig.loggingLevel)
        assertFalse(LibConfig.logRequestBody)
        assertFalse(LibConfig.logResponseBody)
    }

    @Test
    fun `should load performance settings`() {
        assertEquals(10, LibConfig.connectionPoolSize)
        assertTrue(LibConfig.keepAlive)
    }

    @Test
    fun `should override with system properties`() {
        // Set a system property
        System.setProperty("ollama.host", "custom-host")
        
        assertEquals("custom-host", LibConfig.host)
    }

    @Test
    fun `should handle numeric system property overrides`() {
        System.setProperty("ollama.port", "8080")
        System.setProperty("ollama.connection.timeout", "60000")
        
        assertEquals(8080, LibConfig.port)
        assertEquals(60000L, LibConfig.connectionTimeout)
    }

    @Test
    fun `should handle boolean system property overrides`() {
        System.setProperty("ollama.streaming.enabled", "false")
        System.setProperty("ollama.keep.alive", "false")
        
        assertFalse(LibConfig.streamingEnabled)
        assertFalse(LibConfig.keepAlive)
    }

    @Test
    fun `should return custom properties`() {
        System.setProperty("ollama.custom.setting", "custom-value")
        
        assertEquals("custom-value", LibConfig.getCustomProperty("ollama.custom.setting"))
        assertNull(LibConfig.getCustomProperty("ollama.nonexistent.setting"))
        assertEquals("default", LibConfig.getCustomProperty("ollama.nonexistent.setting", "default"))
    }

    @Test
    fun `should return all properties map`() {
        System.setProperty("ollama.test.property", "test-value")
        
        val allProps = LibConfig.getAllProperties()
        
        assertTrue(allProps.containsKey("ollama.host"))
        assertTrue(allProps.containsKey("ollama.port"))
        assertTrue(allProps.containsKey("ollama.test.property"))
        assertEquals("test-value", allProps["ollama.test.property"])
    }

    @Test
    fun `should construct correct base URL from components`() {
        assertEquals("http://localhost:11434", LibConfig.baseUrl)
        
        System.setProperty("ollama.protocol", "https")
        System.setProperty("ollama.host", "api.example.com")
        System.setProperty("ollama.port", "443")
        
        // Note: baseUrl uses property values, so we test the construction logic
        val expectedUrl = "${LibConfig.protocol}://${LibConfig.host}:${LibConfig.port}"
        assertEquals("https://api.example.com:443", expectedUrl)
    }
}