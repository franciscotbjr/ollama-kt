package org.ollamakt.models.responses

import kotlinx.serialization.Serializable
import org.ollamakt.models.domain.Message
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Response from chat completion.
 */
@Serializable
data class ChatResponse(
    val model: String,
    val createdAt: Instant? = null,
    val message: Message,
    val done: Boolean,
    val doneReason: String? = null,
    val totalDuration: Duration? = null,
    val loadDuration: Duration? = null,
    val promptEvalCount: Int? = null,
    val promptEvalDuration: Duration? = null,
    val evalCount: Int? = null,
    val evalDuration: Duration? = null
)