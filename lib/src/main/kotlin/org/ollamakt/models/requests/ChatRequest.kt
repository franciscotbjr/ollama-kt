package org.ollamakt.models.requests

import kotlinx.serialization.Serializable
import org.ollamakt.models.domain.*
import kotlin.time.Duration

/**
 * Request for chat completion.
 */
@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false,
    val format: ResponseFormat? = null,
    val keepAlive: Duration? = null,
    val tools: List<Tool>? = null,
    val think: ThinkingLevel? = null,
    val options: ModelOptions? = null
)