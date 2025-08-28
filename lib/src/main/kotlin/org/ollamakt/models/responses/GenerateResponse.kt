package org.ollamakt.models.responses

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Response from text generation.
 */
@Serializable
data class GenerateResponse(
    val model: String,
    val createdAt: Instant? = null,
    val response: String,
    val done: Boolean,
    val context: List<Int>? = null,
    val totalDuration: Duration? = null,
    val loadDuration: Duration? = null,
    val promptEvalCount: Int? = null,
    val promptEvalDuration: Duration? = null,
    val evalCount: Int? = null,
    val evalDuration: Duration? = null
)