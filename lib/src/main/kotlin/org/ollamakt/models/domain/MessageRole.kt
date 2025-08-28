package org.ollamakt.models.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum representing the role of a message in a chat conversation.
 */
@Serializable
enum class MessageRole {
    @SerialName("user")
    USER,
    
    @SerialName("assistant")
    ASSISTANT,
    
    @SerialName("system")
    SYSTEM,
    
    @SerialName("tool")
    TOOL
}