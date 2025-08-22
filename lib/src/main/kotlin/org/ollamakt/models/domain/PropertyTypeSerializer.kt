package org.ollamakt.models.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Custom serializer for PropertyType to handle both single type (string) and union types (string[]).
 */
object PropertyTypeSerializer : KSerializer<PropertyType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PropertyType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PropertyType) {
        when {
            value.singleType != null -> encoder.encodeString(value.singleType)
            value.unionTypes != null -> {
                val jsonEncoder = encoder as JsonEncoder
                jsonEncoder.encodeJsonElement(JsonArray(value.unionTypes.map { JsonPrimitive(it) }))
            }
            else -> throw SerializationException("PropertyType must have either singleType or unionTypes")
        }
    }

    override fun deserialize(decoder: Decoder): PropertyType {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        
        return when (element) {
            is JsonPrimitive -> PropertyType(element.content)
            is JsonArray -> PropertyType(element.map { it.jsonPrimitive.content })
            else -> throw SerializationException("Expected string or array for PropertyType")
        }
    }
}