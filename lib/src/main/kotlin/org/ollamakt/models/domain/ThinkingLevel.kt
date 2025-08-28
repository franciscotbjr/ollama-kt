package org.ollamakt.models.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum representing the thinking level for model reasoning.
 */
@Serializable
enum class ThinkingLevel {
    @SerialName("false") DISABLED,
    @SerialName("low") LOW,
    @SerialName("medium") MEDIUM,
    @SerialName("high") HIGH
}