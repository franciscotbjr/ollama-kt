package org.ollamakt.models.requests

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Request to pull a model from registry.
 */
@Serializable
data class PullRequest(
    val model: String,
    val insecure: Boolean = false,
    val stream: Boolean = false
)

/**
 * Request to push a model to registry.
 */
@Serializable
data class PushRequest(
    val model: String,
    val insecure: Boolean = false,
    val stream: Boolean = false
)

/**
 * Request to create a new model.
 */
@Serializable
data class CreateRequest(
    val model: String,
    val from: String? = null,
    val modelfile: String? = null,
    val system: String? = null,
    val adapter: String? = null,
    val license: String? = null,
    val template: String? = null,
    val stream: Boolean = false
)

/**
 * Request to delete a model.
 */
@Serializable
data class DeleteRequest(
    val model: String
)

/**
 * Request to copy a model.
 */
@Serializable
data class CopyRequest(
    val source: String,
    val destination: String
)

/**
 * Request to show model information.
 */
@Serializable
data class ShowRequest(
    val model: String
)

/**
 * Request for embeddings.
 */
@Serializable
data class EmbedRequest(
    val model: String,
    val prompt: String,
    val options: Map<String, JsonElement>? = null
)