package org.ollamakt.models.domain

import kotlinx.serialization.Serializable

/**
 * Represents a message in a chat conversation.
 */
@Serializable
data class Message(
    val role: MessageRole,
    val content: String,
    val thinking: String? = null,
    val images: List<String>? = null,
    val toolCalls: List<ToolCall>? = null,
    val toolName: String? = null
)