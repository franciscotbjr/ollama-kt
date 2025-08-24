package org.ollamakt.config

import java.io.IOException
import java.util.*

object LibConfig {
    
    private const val CONFIG_FILE = "ollama-kt.properties"
    private val properties: Properties by lazy { loadProperties() }
    
    private fun loadProperties(): Properties {
        val props = Properties()
        
        try {
            LibConfig::class.java.classLoader.getResourceAsStream(CONFIG_FILE)?.use { inputStream ->
                props.load(inputStream)
            } ?: throw IOException("Configuration file $CONFIG_FILE not found in resources")
        } catch (e: IOException) {
            println("Warning: Could not load configuration file $CONFIG_FILE, using defaults")
        }
        
        return props
    }
    
    // Server Configuration
    val host: String
        get() = getProperty("ollama.host", "localhost")
    
    val port: Int
        get() = getProperty("ollama.port", "11434").toInt()
    
    val protocol: String
        get() = getProperty("ollama.protocol", "http")
    
    val baseUrl: String
        get() = getProperty("ollama.base.url", "$protocol://$host:$port")
    
    // Connection Settings
    val connectionTimeout: Long
        get() = getProperty("ollama.connection.timeout", "30000").toLong()
    
    val requestTimeout: Long
        get() = getProperty("ollama.request.timeout", "300000").toLong()
    
    val socketTimeout: Long
        get() = getProperty("ollama.socket.timeout", "30000").toLong()
    
    // Client Settings
    val userAgent: String
        get() = getProperty("ollama.user.agent", "ollama-kt/1.0.0")
    
    val maxRetries: Int
        get() = getProperty("ollama.max.retries", "3").toInt()
    
    val retryDelay: Long
        get() = getProperty("ollama.retry.delay", "1000").toLong()
    
    // Streaming Configuration
    val streamingEnabled: Boolean
        get() = getProperty("ollama.streaming.enabled", "true").toBoolean()
    
    val streamingBufferSize: Int
        get() = getProperty("ollama.streaming.buffer.size", "8192").toInt()
    
    // Model Defaults
    val defaultModel: String
        get() = getProperty("ollama.default.model", "llama3.2")
    
    val defaultTemperature: Double
        get() = getProperty("ollama.default.temperature", "0.8").toDouble()
    
    val defaultTopP: Double
        get() = getProperty("ollama.default.top.p", "0.9").toDouble()
    
    val defaultTopK: Int
        get() = getProperty("ollama.default.top.k", "40").toInt()
    
    // Response Format
    val defaultResponseFormat: String
        get() = getProperty("ollama.default.response.format", "json")
    
    val responseIncludeContext: Boolean
        get() = getProperty("ollama.response.include.context", "false").toBoolean()
    
    // Logging
    val loggingEnabled: Boolean
        get() = getProperty("ollama.logging.enabled", "true").toBoolean()
    
    val loggingLevel: String
        get() = getProperty("ollama.logging.level", "INFO")
    
    val logRequestBody: Boolean
        get() = getProperty("ollama.logging.request.body", "false").toBoolean()
    
    val logResponseBody: Boolean
        get() = getProperty("ollama.logging.response.body", "false").toBoolean()
    
    // Performance
    val connectionPoolSize: Int
        get() = getProperty("ollama.connection.pool.size", "10").toInt()
    
    val keepAlive: Boolean
        get() = getProperty("ollama.keep.alive", "true").toBoolean()
    
    private fun getProperty(key: String, defaultValue: String): String {
        return System.getProperty(key) ?: properties.getProperty(key, defaultValue)
    }
    
    fun getCustomProperty(key: String, defaultValue: String? = null): String? {
        return System.getProperty(key) ?: properties.getProperty(key, defaultValue)
    }
    
    fun getAllProperties(): Map<String, String> {
        val systemProps = System.getProperties().stringPropertyNames()
            .filter { it.startsWith("ollama.") }
            .associateWith { System.getProperty(it) }
        
        val fileProps = properties.stringPropertyNames()
            .associateWith { properties.getProperty(it) }
        
        return fileProps + systemProps
    }
}