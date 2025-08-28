package org.ollamakt.models.responses

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

/**
 * Response for model listing.
 */
@Serializable
data class ListResponse(
    val models: List<ModelInfo>
)

/**
 * Information about a model.
 */
@Serializable
data class ModelInfo(
    val name: String,
    val size: Long,
    val digest: String,
    val modifiedAt: Instant? = null,
    val details: ModelDetails? = null
)

/**
 * Detailed information about a model.
 */
@Serializable
data class ModelDetails(
    val parent: String? = null,
    val format: String? = null,
    val family: String? = null,
    val families: List<String>? = null,
    val parameterSize: String? = null,
    val quantizationLevel: String? = null
)

/**
 * Response for progress operations (pull, push, create).
 */
@Serializable
data class ProgressResponse(
    val status: String,
    val digest: String? = null,
    val total: Long = 0,
    val completed: Long = 0
)

/**
 * Response for status operations.
 */
@Serializable
data class StatusResponse(
    val success: Boolean,
    val message: String? = null
)

/**
 * Response for show model information.
 */
@Serializable
data class ShowResponse(
    val license: String? = null,
    val modelfile: String? = null,
    val parameters: String? = null,
    val template: String? = null,
    val system: String? = null,
    val details: ModelDetails? = null
)

/**
 * Response for embeddings.
 */
@Serializable
data class EmbedResponse(
    val embedding: List<Double>
)

/**
 * Response for create model operations.
 */
@Serializable
data class CreateResponse(
    val status: String
)

/**
 * Response for delete model operations.
 */
@Serializable
data class DeleteResponse(
    val success: Boolean = true
)

/**
 * Response for copy model operations.
 */
@Serializable
data class CopyResponse(
    val success: Boolean = true
)

/**
 * Response for pull model operations.
 */
@Serializable
data class PullResponse(
    val status: String
)

/**
 * Response for push model operations.
 */
@Serializable
data class PushResponse(
    val status: String
)

/**
 * Response for process listing.
 */
@Serializable
data class ProcessListResponse(
    val models: List<ProcessInfo>
)

/**
 * Information about a running process.
 */
@Serializable
data class ProcessInfo(
    val name: String,
    val size: Long,
    val digest: String,
    val details: ModelDetails? = null,
    val expiresAt: Instant? = null,
    val sizeVram: Long? = null
)