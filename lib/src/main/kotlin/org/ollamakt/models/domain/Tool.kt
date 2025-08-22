package org.ollamakt.models.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a tool available to the model.
 */
@Serializable
data class Tool(
    val type: String,
    val function: ToolFunction
)

/**
 * Represents the function definition of a tool.
 */
@Serializable
data class ToolFunction(
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val parameters: ToolFunctionParameters? = null
)

/**
 * Represents the parameters schema for a tool function.
 */
@Serializable
data class ToolFunctionParameters(
    val type: String? = null,
    @SerialName("\$defs")
    val defs: Map<String, SchemaDefinition>? = null,
    val items: SchemaItems? = null,
    val required: List<String>? = null,
    val properties: Map<String, PropertyDefinition>? = null
)

/**
 * Represents a schema definition in $defs.
 */
@Serializable
data class SchemaDefinition(
    val type: String? = null,
    val properties: Map<String, PropertyDefinition>? = null,
    val required: List<String>? = null,
    val items: SchemaItems? = null,
    val enum: List<String>? = null,
    val description: String? = null
)

/**
 * Represents items schema for array types.
 */
@Serializable
data class SchemaItems(
    val type: String? = null,
    val properties: Map<String, PropertyDefinition>? = null,
    val enum: List<String>? = null,
    val items: SchemaItems? = null // For nested arrays
)

/**
 * Represents a property definition within a schema.
 */
@Serializable
data class PropertyDefinition(
    val type: PropertyType? = null,
    val items: SchemaItems? = null,
    val description: String? = null,
    val enum: List<String>? = null,
    val properties: Map<String, PropertyDefinition>? = null, // For nested objects
    val required: List<String>? = null,
    @SerialName("\$ref")
    val ref: String? = null // For referencing $defs
)

/**
 * Represents property types, supporting both single type and union types.
 */
@Serializable(with = PropertyTypeSerializer::class)
data class PropertyType(
    val singleType: String? = null,
    val unionTypes: List<String>? = null
) {
    constructor(type: String) : this(singleType = type, unionTypes = null)
    constructor(types: List<String>) : this(singleType = null, unionTypes = types)
    
    fun isArray(): Boolean = singleType == "array"
    fun isObject(): Boolean = singleType == "object"
    fun isString(): Boolean = singleType == "string"
    fun isNumber(): Boolean = singleType == "number" || singleType == "integer"
    fun isBoolean(): Boolean = singleType == "boolean"
}
