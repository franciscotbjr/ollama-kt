package org.ollamakt.models.domain

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("PropertyTypeSerializer Tests")
class PropertyTypeSerializerTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Nested
    @DisplayName("Serialization Tests")
    inner class SerializationTests {

        @Test
        @DisplayName("Should serialize single type as string")
        fun `should serialize single type as string`() {
            val propertyType = PropertyType("string")
            val result = json.encodeToString(propertyType)
            
            assertEquals("\"string\"", result)
        }

        @Test
        @DisplayName("Should serialize union types as array")
        fun `should serialize union types as array`() {
            val propertyType = PropertyType(listOf("string", "number", "boolean"))
            val result = json.encodeToString(propertyType)
            
            // Parse the result to verify it's a valid JSON array
            val expectedTypes = listOf("string", "number", "boolean")
            val resultArray = json.decodeFromString<List<String>>(result)
            
            assertEquals(expectedTypes, resultArray)
        }

        @Test
        @DisplayName("Should serialize empty union types as empty array")
        fun `should serialize empty union types as empty array`() {
            val propertyType = PropertyType(emptyList())
            val result = json.encodeToString(propertyType)
            
            assertEquals("[]", result)
        }

        @Test
        @DisplayName("Should throw exception for invalid PropertyType")
        fun `should throw exception for invalid PropertyType`() {
            val invalidPropertyType = PropertyType(singleType = null, unionTypes = null)
            
            assertThrows<SerializationException> {
                json.encodeToString(invalidPropertyType)
            }
        }

        @Test
        @DisplayName("Should serialize different primitive types")
        fun `should serialize different primitive types`() {
            val testCases = listOf(
                "string",
                "number", 
                "integer",
                "boolean",
                "array",
                "object",
                "null"
            )
            
            testCases.forEach { type ->
                val propertyType = PropertyType(type)
                val result = json.encodeToString(propertyType)
                assertEquals("\"$type\"", result)
            }
        }
    }

    @Nested
    @DisplayName("Deserialization Tests")
    inner class DeserializationTests {

        @Test
        @DisplayName("Should deserialize string to single type")
        fun `should deserialize string to single type`() {
            val jsonString = "\"string\""
            val result = json.decodeFromString<PropertyType>(jsonString)
            
            assertEquals("string", result.singleType)
            assertNull(result.unionTypes)
        }

        @Test
        @DisplayName("Should deserialize array to union types")
        fun `should deserialize array to union types`() {
            val jsonString = "[\"string\", \"number\", \"boolean\"]"
            val result = json.decodeFromString<PropertyType>(jsonString)
            
            assertNull(result.singleType)
            assertEquals(listOf("string", "number", "boolean"), result.unionTypes)
        }

        @Test
        @DisplayName("Should deserialize empty array to empty union types")
        fun `should deserialize empty array to empty union types`() {
            val jsonString = "[]"
            val result = json.decodeFromString<PropertyType>(jsonString)
            
            assertNull(result.singleType)
            assertEquals(emptyList<String>(), result.unionTypes)
        }

        @Test
        @DisplayName("Should deserialize single element array to union types")
        fun `should deserialize single element array to union types`() {
            val jsonString = "[\"string\"]"
            val result = json.decodeFromString<PropertyType>(jsonString)
            
            assertNull(result.singleType)
            assertEquals(listOf("string"), result.unionTypes)
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON")
        fun `should throw exception for invalid JSON`() {
            val invalidJsonCases = listOf(
                "{}",         // object
                "{\"invalid\": \"structure\"}"  // invalid object structure
            )
            
            invalidJsonCases.forEach { invalidJson ->
                assertThrows<SerializationException> {
                    json.decodeFromString<PropertyType>(invalidJson)
                }
            }
        }

        @Test
        @DisplayName("Should handle TypeScript-like type definitions")
        fun `should handle TypeScript-like type definitions`() {
            // Common TypeScript patterns
            val testCases = mapOf(
                "\"string\"" to PropertyType("string"),
                "[\"string\", \"number\"]" to PropertyType(listOf("string", "number")),
                "[\"string\", \"number\", \"null\"]" to PropertyType(listOf("string", "number", "null")),
                "\"boolean\"" to PropertyType("boolean"),
                "\"object\"" to PropertyType("object")
            )
            
            testCases.forEach { (jsonString, expected) ->
                val result = json.decodeFromString<PropertyType>(jsonString)
                
                if (expected.singleType != null) {
                    assertEquals(expected.singleType, result.singleType)
                    assertNull(result.unionTypes)
                } else {
                    assertNull(result.singleType)
                    assertEquals(expected.unionTypes, result.unionTypes)
                }
            }
        }
    }

    @Nested
    @DisplayName("Round-trip Tests")
    inner class RoundTripTests {

        @Test
        @DisplayName("Should maintain data integrity in round-trip serialization")
        fun `should maintain data integrity in round-trip serialization`() {
            val testCases = listOf(
                PropertyType("string"),
                PropertyType("number"),
                PropertyType("boolean"),
                PropertyType(listOf("string", "number")),
                PropertyType(listOf("string", "number", "boolean", "null")),
                PropertyType(emptyList())
            )
            
            testCases.forEach { original ->
                val serialized = json.encodeToString(original)
                val deserialized = json.decodeFromString<PropertyType>(serialized)
                
                assertEquals(original.singleType, deserialized.singleType)
                assertEquals(original.unionTypes, deserialized.unionTypes)
            }
        }

        @Test
        @DisplayName("Should handle complex scenarios from real APIs")
        fun `should handle complex scenarios from real APIs`() {
            // Test cases that might appear in real tool definitions
            val realWorldCases = listOf(
                // OpenAI function calling patterns
                "\"string\"",
                "\"number\"", 
                "\"integer\"",
                "\"boolean\"",
                "\"array\"",
                "\"object\"",
                
                // Union types
                "[\"string\", \"null\"]",
                "[\"number\", \"string\"]",
                "[\"boolean\", \"string\", \"number\"]"
            )
            
            realWorldCases.forEach { jsonCase ->
                assertDoesNotThrow {
                    val deserialized = json.decodeFromString<PropertyType>(jsonCase)
                    val reSerialized = json.encodeToString(deserialized)
                    val reDeserialized = json.decodeFromString<PropertyType>(reSerialized)
                    
                    assertEquals(deserialized.singleType, reDeserialized.singleType)
                    assertEquals(deserialized.unionTypes, reDeserialized.unionTypes)
                }
            }
        }
    }

    @Nested
    @DisplayName("Integration with Tool Structure")
    inner class IntegrationTests {

        @Test
        @DisplayName("Should work correctly within complete Tool structure")
        fun `should work correctly within complete Tool structure`() {
            val tool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "test_function",
                    description = "Test function with various property types",
                    parameters = ToolFunctionParameters(
                        type = "object",
                        properties = mapOf(
                            "string_prop" to PropertyDefinition(type = PropertyType("string")),
                            "union_prop" to PropertyDefinition(type = PropertyType(listOf("string", "number"))),
                            "array_prop" to PropertyDefinition(type = PropertyType("array")),
                            "nullable_prop" to PropertyDefinition(type = PropertyType(listOf("string", "null")))
                        )
                    )
                )
            )
            
            val serialized = json.encodeToString(tool)
            val deserialized = json.decodeFromString<Tool>(serialized)
            
            // Verify all property types are preserved
            val props = deserialized.function.parameters?.properties
            assertNotNull(props)
            
            assertEquals("string", props!!["string_prop"]?.type?.singleType)
            assertEquals(listOf("string", "number"), props["union_prop"]?.type?.unionTypes)
            assertEquals("array", props["array_prop"]?.type?.singleType)
            assertEquals(listOf("string", "null"), props["nullable_prop"]?.type?.unionTypes)
        }

        @Test
        @DisplayName("Should handle malformed JSON gracefully")
        fun `should handle malformed JSON gracefully`() {
            // Test cases that should actually fail during PropertyDefinition deserialization
            val malformedCases = listOf(
                "{ \"type\": {} }",         // object instead of string/array for PropertyType
                "{ \"properties\": \"invalid\" }", // wrong type for properties
            )
            
            malformedCases.forEach { malformed ->
                assertThrows<Exception> {
                    // Try to deserialize as a PropertyDefinition that contains PropertyType
                    json.decodeFromString<PropertyDefinition>(malformed)
                }
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should handle large union type arrays efficiently")
        fun `should handle large union type arrays efficiently`() {
            val largeUnionTypes = (1..100).map { "type_$it" }
            val propertyType = PropertyType(largeUnionTypes)
            
            // Should not throw any exceptions and complete in reasonable time
            assertDoesNotThrow {
                val serialized = json.encodeToString(propertyType)
                val deserialized = json.decodeFromString<PropertyType>(serialized)
                
                assertEquals(largeUnionTypes, deserialized.unionTypes)
            }
        }

        @Test
        @DisplayName("Should handle repeated serialization/deserialization")
        fun `should handle repeated serialization and deserialization`() {
            val original = PropertyType(listOf("string", "number", "boolean"))
            
            var current = original
            
            // Perform multiple round-trips
            repeat(10) {
                val serialized = json.encodeToString(current)
                current = json.decodeFromString(serialized)
            }
            
            // Data should remain unchanged
            assertEquals(original.singleType, current.singleType)
            assertEquals(original.unionTypes, current.unionTypes)
        }
    }
}