package org.ollamakt.models.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Represents a tool call made by the model.
 */
@Serializable
data class ToolCall(
    val function: ToolCallFunction
)

/**
 * Represents the function details of a tool call.
 */
@Serializable
data class ToolCallFunction(
    val name: String,
    val arguments: Map<String, JsonElement>
)