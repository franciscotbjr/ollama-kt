package org.ollamakt.models.requests

import kotlinx.serialization.Serializable
import org.ollamakt.models.domain.ModelOptions
import org.ollamakt.models.domain.ResponseFormat
import kotlin.time.Duration

/**
 * Request for text generation.
 */
@Serializable
data class GenerateRequest(
    val model: String,
    val prompt: String,
    val images: List<String>? = null,
    val stream: Boolean = false,
    val format: ResponseFormat? = null,
    val keepAlive: Duration? = null,
    val options: ModelOptions? = null,
    val system: String? = null,
    val template: String? = null,
    val context: List<Int>? = null,
    val raw: Boolean = false
)