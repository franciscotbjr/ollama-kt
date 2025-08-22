package org.ollamakt.models.domain

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("Tool Domain Classes Tests")
class ToolTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Nested
    @DisplayName("PropertyType Tests")
    inner class PropertyTypeTests {

        @Test
        @DisplayName("Should create PropertyType with single type")
        fun `should create PropertyType with single type`() {
            val propertyType = PropertyType("string")
            
            assertEquals("string", propertyType.singleType)
            assertNull(propertyType.unionTypes)
            assertTrue(propertyType.isString())
            assertFalse(propertyType.isNumber())
        }

        @Test
        @DisplayName("Should create PropertyType with union types")
        fun `should create PropertyType with union types`() {
            val propertyType = PropertyType(listOf("string", "number"))
            
            assertNull(propertyType.singleType)
            assertEquals(listOf("string", "number"), propertyType.unionTypes)
        }

        @Test
        @DisplayName("Should correctly identify type categories")
        fun `should correctly identify type categories`() {
            assertTrue(PropertyType("array").isArray())
            assertTrue(PropertyType("object").isObject())
            assertTrue(PropertyType("string").isString())
            assertTrue(PropertyType("number").isNumber())
            assertTrue(PropertyType("integer").isNumber())
            assertTrue(PropertyType("boolean").isBoolean())
        }

        @Test
        @DisplayName("Should serialize single type correctly")
        fun `should serialize single type correctly`() {
            val propertyType = PropertyType("string")
            val serialized = json.encodeToString(propertyType)
            
            assertEquals("\"string\"", serialized)
        }

        @Test
        @DisplayName("Should serialize union types correctly")
        fun `should serialize union types correctly`() {
            val propertyType = PropertyType(listOf("string", "number"))
            val serialized = json.encodeToString(propertyType)
            
            assertTrue(serialized.contains("["))
            assertTrue(serialized.contains("\"string\""))
            assertTrue(serialized.contains("\"number\""))
        }

        @Test
        @DisplayName("Should deserialize single type correctly")
        fun `should deserialize single type correctly`() {
            val jsonString = "\"string\""
            val propertyType = json.decodeFromString<PropertyType>(jsonString)
            
            assertEquals("string", propertyType.singleType)
            assertNull(propertyType.unionTypes)
        }

        @Test
        @DisplayName("Should deserialize union types correctly")
        fun `should deserialize union types correctly`() {
            val jsonString = "[\"string\", \"number\"]"
            val propertyType = json.decodeFromString<PropertyType>(jsonString)
            
            assertNull(propertyType.singleType)
            assertEquals(listOf("string", "number"), propertyType.unionTypes)
        }
    }

    @Nested
    @DisplayName("PropertyDefinition Tests")
    inner class PropertyDefinitionTests {

        @Test
        @DisplayName("Should create PropertyDefinition with all fields")
        fun `should create PropertyDefinition with all fields`() {
            val propertyDef = PropertyDefinition(
                type = PropertyType("string"),
                description = "A string property",
                enum = listOf("option1", "option2")
            )
            
            assertEquals("string", propertyDef.type?.singleType)
            assertEquals("A string property", propertyDef.description)
            assertEquals(listOf("option1", "option2"), propertyDef.enum)
        }

        @Test
        @DisplayName("Should serialize PropertyDefinition correctly")
        fun `should serialize PropertyDefinition correctly`() {
            val propertyDef = PropertyDefinition(
                type = PropertyType("string"),
                description = "Test property"
            )
            
            val serialized = json.encodeToString(propertyDef)
            
            assertTrue(serialized.contains("\"type\""))
            assertTrue(serialized.contains("\"string\""))
            assertTrue(serialized.contains("\"description\""))
            assertTrue(serialized.contains("\"Test property\""))
        }

        @Test
        @DisplayName("Should handle nested properties")
        fun `should handle nested properties`() {
            val nestedProperty = PropertyDefinition(
                type = PropertyType("object"),
                properties = mapOf(
                    "nested_field" to PropertyDefinition(
                        type = PropertyType("string"),
                        description = "Nested string field"
                    )
                )
            )
            
            assertNotNull(nestedProperty.properties)
            assertEquals(1, nestedProperty.properties?.size)
            assertEquals("string", nestedProperty.properties?.get("nested_field")?.type?.singleType)
        }
    }

    @Nested
    @DisplayName("SchemaItems Tests")
    inner class SchemaItemsTests {

        @Test
        @DisplayName("Should create SchemaItems for array type")
        fun `should create SchemaItems for array type`() {
            val schemaItems = SchemaItems(
                type = "string",
                enum = listOf("value1", "value2")
            )
            
            assertEquals("string", schemaItems.type)
            assertEquals(listOf("value1", "value2"), schemaItems.enum)
        }

        @Test
        @DisplayName("Should handle nested array items")
        fun `should handle nested array items`() {
            val nestedItems = SchemaItems(
                type = "array",
                items = SchemaItems(type = "string")
            )
            
            assertEquals("array", nestedItems.type)
            assertEquals("string", nestedItems.items?.type)
        }

        @Test
        @DisplayName("Should serialize SchemaItems correctly")
        fun `should serialize SchemaItems correctly`() {
            val schemaItems = SchemaItems(type = "string")
            val serialized = json.encodeToString(schemaItems)
            
            assertTrue(serialized.contains("\"type\""))
            assertTrue(serialized.contains("\"string\""))
        }
    }

    @Nested
    @DisplayName("ToolFunctionParameters Tests")
    inner class ToolFunctionParametersTests {

        @Test
        @DisplayName("Should create ToolFunctionParameters with properties")
        fun `should create ToolFunctionParameters with properties`() {
            val parameters = ToolFunctionParameters(
                type = "object",
                required = listOf("param1", "param2"),
                properties = mapOf(
                    "param1" to PropertyDefinition(
                        type = PropertyType("string"),
                        description = "First parameter"
                    ),
                    "param2" to PropertyDefinition(
                        type = PropertyType("number"),
                        description = "Second parameter"
                    )
                )
            )
            
            assertEquals("object", parameters.type)
            assertEquals(listOf("param1", "param2"), parameters.required)
            assertEquals(2, parameters.properties?.size)
        }

        @Test
        @DisplayName("Should handle defs serialization")
        fun `should handle defs serialization`() {
            val parameters = ToolFunctionParameters(
                type = "object",
                defs = mapOf(
                    "CustomType" to SchemaDefinition(
                        type = "object",
                        properties = mapOf(
                            "field" to PropertyDefinition(type = PropertyType("string"))
                        )
                    )
                )
            )
            
            val serialized = json.encodeToString(parameters)
            assertTrue(serialized.contains("\"\$defs\""))
            assertTrue(serialized.contains("\"CustomType\""))
        }
    }

    @Nested
    @DisplayName("ToolFunction Tests")
    inner class ToolFunctionTests {

        @Test
        @DisplayName("Should create ToolFunction with all fields")
        fun `should create ToolFunction with all fields`() {
            val toolFunction = ToolFunction(
                name = "test_function",
                description = "A test function",
                type = "function",
                parameters = ToolFunctionParameters(
                    type = "object",
                    properties = mapOf(
                        "input" to PropertyDefinition(type = PropertyType("string"))
                    )
                )
            )
            
            assertEquals("test_function", toolFunction.name)
            assertEquals("A test function", toolFunction.description)
            assertEquals("function", toolFunction.type)
            assertNotNull(toolFunction.parameters)
        }

        @Test
        @DisplayName("Should serialize ToolFunction correctly")
        fun `should serialize ToolFunction correctly`() {
            val toolFunction = ToolFunction(
                name = "calculator",
                description = "Basic calculator"
            )
            
            val serialized = json.encodeToString(toolFunction)
            
            assertTrue(serialized.contains("\"name\""))
            assertTrue(serialized.contains("\"calculator\""))
            assertTrue(serialized.contains("\"description\""))
            assertTrue(serialized.contains("\"Basic calculator\""))
        }
    }

    @Nested
    @DisplayName("Tool Tests")
    inner class ToolTests {

        @Test
        @DisplayName("Should create complete Tool with function")
        fun `should create complete Tool with function`() {
            val tool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "weather",
                    description = "Get weather information",
                    parameters = ToolFunctionParameters(
                        type = "object",
                        required = listOf("location"),
                        properties = mapOf(
                            "location" to PropertyDefinition(
                                type = PropertyType("string"),
                                description = "City name"
                            )
                        )
                    )
                )
            )
            
            assertEquals("function", tool.type)
            assertEquals("weather", tool.function.name)
            assertEquals("Get weather information", tool.function.description)
        }

        @Test
        @DisplayName("Should serialize and deserialize Tool correctly")
        fun `should serialize and deserialize Tool correctly`() {
            val originalTool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "calculator",
                    description = "Math operations",
                    parameters = ToolFunctionParameters(
                        type = "object",
                        required = listOf("operation", "a", "b"),
                        properties = mapOf(
                            "operation" to PropertyDefinition(
                                type = PropertyType("string"),
                                enum = listOf("add", "subtract", "multiply", "divide")
                            ),
                            "a" to PropertyDefinition(type = PropertyType("number")),
                            "b" to PropertyDefinition(type = PropertyType("number"))
                        )
                    )
                )
            )
            
            val serialized = json.encodeToString(originalTool)
            val deserialized = json.decodeFromString<Tool>(serialized)
            
            assertEquals(originalTool.type, deserialized.type)
            assertEquals(originalTool.function.name, deserialized.function.name)
            assertEquals(originalTool.function.description, deserialized.function.description)
            assertEquals(originalTool.function.parameters?.required, deserialized.function.parameters?.required)
        }

        @Test
        @DisplayName("Should handle complex nested Tool structure")
        fun `should handle complex nested Tool structure`() {
            val tool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "complex_function",
                    description = "Complex nested structure",
                    parameters = ToolFunctionParameters(
                        type = "object",
                        properties = mapOf(
                            "nested_object" to PropertyDefinition(
                                type = PropertyType("object"),
                                properties = mapOf(
                                    "inner_array" to PropertyDefinition(
                                        type = PropertyType("array"),
                                        items = SchemaItems(
                                            type = "object",
                                            properties = mapOf(
                                                "item_field" to PropertyDefinition(type = PropertyType("string"))
                                            )
                                        )
                                    )
                                )
                            ),
                            "union_type" to PropertyDefinition(
                                type = PropertyType(listOf("string", "number", "null"))
                            )
                        )
                    )
                )
            )
            
            val serialized = json.encodeToString(tool)
            val deserialized = json.decodeFromString<Tool>(serialized)
            
            assertEquals(tool.function.name, deserialized.function.name)
            assertNotNull(deserialized.function.parameters?.properties?.get("nested_object"))
            assertNotNull(deserialized.function.parameters?.properties?.get("union_type"))
        }
    }

    @Nested
    @DisplayName("Real-world Tool Examples Tests")
    inner class RealWorldExamplesTests {

        @Test
        @DisplayName("Should create and validate calculator tool")
        fun `should create and validate calculator tool`() {
            val calculatorTool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "calculator",
                    description = "Perform basic mathematical calculations",
                    parameters = ToolFunctionParameters(
                        type = "object",
                        required = listOf("operation", "a", "b"),
                        properties = mapOf(
                            "operation" to PropertyDefinition(
                                type = PropertyType("string"),
                                description = "The mathematical operation to perform",
                                enum = listOf("add", "subtract", "multiply", "divide")
                            ),
                            "a" to PropertyDefinition(
                                type = PropertyType("number"),
                                description = "First number"
                            ),
                            "b" to PropertyDefinition(
                                type = PropertyType("number"), 
                                description = "Second number"
                            )
                        )
                    )
                )
            )
            
            assertEquals("function", calculatorTool.type)
            assertEquals("calculator", calculatorTool.function.name)
            
            val properties = calculatorTool.function.parameters?.properties
            assertNotNull(properties)
            assertTrue(properties!!.containsKey("operation"))
            assertTrue(properties.containsKey("a"))
            assertTrue(properties.containsKey("b"))
            
            val operationEnum = properties["operation"]?.enum
            assertEquals(listOf("add", "subtract", "multiply", "divide"), operationEnum)
        }

        @Test
        @DisplayName("Should create and validate weather tool")
        fun `should create and validate weather tool`() {
            val weatherTool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "get_weather",
                    description = "Get weather information for a location",
                    parameters = ToolFunctionParameters(
                        type = "object",
                        required = listOf("location"),
                        properties = mapOf(
                            "location" to PropertyDefinition(
                                type = PropertyType("string"),
                                description = "The city and country"
                            ),
                            "forecast_days" to PropertyDefinition(
                                type = PropertyType(listOf("number", "null")),
                                description = "Number of forecast days (1-7)"
                            )
                        )
                    )
                )
            )
            
            assertEquals("get_weather", weatherTool.function.name)
            
            val properties = weatherTool.function.parameters?.properties
            assertTrue(properties!!.containsKey("location"))
            assertTrue(properties.containsKey("forecast_days"))
            
            // Test union type for forecast_days
            val forecastDaysType = properties["forecast_days"]?.type
            assertEquals(listOf("number", "null"), forecastDaysType?.unionTypes)
        }

        @Test
        @DisplayName("Should create and validate list processor tool")
        fun `should create and validate list processor tool`() {
            val listTool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "process_list",
                    description = "Process a list of items",
                    parameters = ToolFunctionParameters(
                        type = "object",
                        required = listOf("items"),
                        properties = mapOf(
                            "items" to PropertyDefinition(
                                type = PropertyType("array"),
                                description = "Array of strings to process",
                                items = SchemaItems(
                                    type = "string"
                                )
                            )
                        )
                    )
                )
            )
            
            assertEquals("process_list", listTool.function.name)
            
            val itemsProperty = listTool.function.parameters?.properties?.get("items")
            assertTrue(itemsProperty?.type?.isArray() ?: false)
            assertEquals("string", itemsProperty?.items?.type)
        }
    }

    @Nested
    @DisplayName("Helper Functions Tests")
    inner class HelperFunctionsTests {

        private val testTool = Tool(
            type = "function",
            function = ToolFunction(
                name = "calculator",
                description = "Perform basic mathematical calculations",
                parameters = ToolFunctionParameters(
                    type = "object",
                    required = listOf("operation", "a", "b"),
                    properties = mapOf(
                        "operation" to PropertyDefinition(
                            type = PropertyType("string"),
                            description = "The mathematical operation to perform",
                            enum = listOf("add", "subtract", "multiply", "divide")
                        ),
                        "a" to PropertyDefinition(
                            type = PropertyType("number"),
                            description = "First number"
                        ),
                        "b" to PropertyDefinition(
                            type = PropertyType("number"), 
                            description = "Second number"
                        )
                    )
                )
            )
        )

        @Test
        @DisplayName("Should correctly identify required parameters")
        fun `should correctly identify required parameters`() {
            val requiredParams = testTool.function.parameters?.required ?: emptyList()
            assertTrue("operation" in requiredParams)
            assertTrue("a" in requiredParams)
            assertTrue("b" in requiredParams)
            assertFalse("nonexistent" in requiredParams)
        }

        @Test
        @DisplayName("Should get parameter descriptions")
        fun `should get parameter descriptions`() {
            val description = testTool.function.parameters?.properties?.get("operation")?.description
            assertEquals("The mathematical operation to perform", description)
            
            val nonexistentDescription = testTool.function.parameters?.properties?.get("nonexistent")?.description
            assertNull(nonexistentDescription)
        }

        @Test
        @DisplayName("Should validate parameter types correctly")
        fun `should validate parameter types correctly`() {
            val operationProperty = testTool.function.parameters?.properties?.get("operation")
            assertTrue(operationProperty?.type?.isString() ?: false)
            
            val aProperty = testTool.function.parameters?.properties?.get("a")
            assertTrue(aProperty?.type?.isNumber() ?: false)
        }

        @Test
        @DisplayName("Should handle union type validation")
        fun `should handle union type validation`() {
            val weatherTool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "get_weather",
                    parameters = ToolFunctionParameters(
                        properties = mapOf(
                            "forecast_days" to PropertyDefinition(
                                type = PropertyType(listOf("number", "null"))
                            )
                        )
                    )
                )
            )
            
            val forecastDaysProperty = weatherTool.function.parameters?.properties?.get("forecast_days")
            assertEquals(listOf("number", "null"), forecastDaysProperty?.type?.unionTypes)
        }

        @Test
        @DisplayName("Should identify union types correctly")
        fun `should identify union types correctly`() {
            val unionTypeProperty = PropertyDefinition(type = PropertyType(listOf("string", "number")))
            val singleTypeProperty = PropertyDefinition(type = PropertyType("string"))
            
            assertNotNull(unionTypeProperty.type?.unionTypes)
            assertNull(singleTypeProperty.type?.unionTypes)
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty Tool gracefully")
        fun `should handle empty Tool gracefully`() {
            val emptyTool = Tool(
                type = "function",
                function = ToolFunction()
            )
            
            assertEquals("function", emptyTool.type)
            assertNull(emptyTool.function.name)
            assertNull(emptyTool.function.parameters)
        }

        @Test
        @DisplayName("Should handle Tool with minimal parameters")
        fun `should handle Tool with minimal parameters`() {
            val minimalTool = Tool(
                type = "function",
                function = ToolFunction(
                    name = "minimal",
                    parameters = ToolFunctionParameters(type = "object")
                )
            )
            
            val serialized = json.encodeToString(minimalTool)
            val deserialized = json.decodeFromString<Tool>(serialized)
            
            assertEquals("minimal", deserialized.function.name)
            assertEquals("object", deserialized.function.parameters?.type)
        }

        @Test
        @DisplayName("Should handle deeply nested structures")
        fun `should handle deeply nested structures`() {
            val deepNested = PropertyDefinition(
                type = PropertyType("object"),
                properties = mapOf(
                    "level1" to PropertyDefinition(
                        type = PropertyType("object"),
                        properties = mapOf(
                            "level2" to PropertyDefinition(
                                type = PropertyType("object"),
                                properties = mapOf(
                                    "level3" to PropertyDefinition(type = PropertyType("string"))
                                )
                            )
                        )
                    )
                )
            )
            
            val serialized = json.encodeToString(deepNested)
            val deserialized = json.decodeFromString<PropertyDefinition>(serialized)
            
            assertEquals("object", deserialized.type?.singleType)
            assertNotNull(deserialized.properties?.get("level1")?.properties?.get("level2")?.properties?.get("level3"))
        }
    }
}