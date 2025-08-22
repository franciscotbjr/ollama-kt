package org.ollamakt.models.domain

import kotlinx.serialization.Serializable

/**
 * Represents model configuration options.
 */
@Serializable
data class ModelOptions(
    val temperature: Double? = null,
    val topK: Int? = null,
    val topP: Double? = null,
    val maxTokens: Int? = null,
    val repeatPenalty: Double? = null,
    val seed: Int? = null,
    val stop: List<String>? = null
)