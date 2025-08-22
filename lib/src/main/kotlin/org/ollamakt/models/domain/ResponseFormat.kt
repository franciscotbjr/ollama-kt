package org.ollamakt.models.domain

import kotlinx.serialization.Serializable

/**
 * Represents the desired response format from the model.
 */
@Serializable
data class ResponseFormat(
    val type: String
)