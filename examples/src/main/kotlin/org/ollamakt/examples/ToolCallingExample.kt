package org.ollamakt.examples.toolcalling

import org.ollamakt.models.domain.*
import org.ollamakt.models.requests.ChatRequest
import kotlinx.serialization.json.JsonPrimitive

/**
 * Tool calling example showing how to use tools with function calling.
 */
fun main() {
    // Create calculator tool manually since ToolExamples was moved
    val calculatorTool = Tool(
        type = "function",
        function = ToolFunction(
            name = "calculator",
            description = "Perform basic mathematical calculations",
            type = "function",
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
    
    val weatherTool = Tool(
        type = "function",
        function = ToolFunction(
            name = "get_weather",
            description = "Get weather information for a location"
        )
    )
    
    val listTool = Tool(
        type = "function",
        function = ToolFunction(
            name = "process_list",
            description = "Process a list of items"
        )
    )
    
    println("Created tools:")
    println("1. Calculator tool: ${calculatorTool.function.name}")
    println("2. Weather tool: ${weatherTool.function.name}")
    println("3. List processor tool: ${listTool.function.name}")
    
    // Create a chat request with tools
    val toolChatRequest = ChatRequest(
        model = "llama3.1:8b",
        messages = listOf(
            Message(
                role = MessageRole.USER,
                content = "What's 15 + 27?"
            )
        ),
        tools = listOf(calculatorTool)
    )
    
    println("\nChat request with tools:")
    println("Model: ${toolChatRequest.model}")
    println("Tools available: ${toolChatRequest.tools?.size}")
    println("Question: ${toolChatRequest.messages.first().content}")
    
    // Example of tool call response
    val toolCall = ToolCall(
        function = ToolCallFunction(
            name = "calculator",
            arguments = mapOf(
                "operation" to JsonPrimitive("add"),
                "a" to JsonPrimitive(15),
                "b" to JsonPrimitive(27)
            )
        )
    )
    
    println("\nExample tool call:")
    println("Function: ${toolCall.function.name}")
    println("Operation: ${toolCall.function.arguments["operation"]}")
    println("A: ${toolCall.function.arguments["a"]}")
    println("B: ${toolCall.function.arguments["b"]}")
    
    // Create message with tool call
    val messageWithToolCall = Message(
        role = MessageRole.ASSISTANT,
        content = "I'll calculate that for you.",
        toolCalls = listOf(toolCall)
    )
    
    // Create tool response message
    val toolResponse = Message(
        role = MessageRole.TOOL,
        content = "42",
        toolName = "calculator"
    )
    
    println("\nTool calling flow:")
    println("1. Assistant message: ${messageWithToolCall.content}")
    println("2. Tool called: ${messageWithToolCall.toolCalls?.first()?.function?.name}")
    println("3. Tool response: ${toolResponse.content}")
    
    // Simple tool validation examples
    println("\nTool validation:")
    val operationProperty = calculatorTool.function.parameters?.properties?.get("operation")
    println("Operation parameter exists: ${operationProperty != null}")
    println("Operation description: ${operationProperty?.description}")
    
    val requiredParams = calculatorTool.function.parameters?.required ?: emptyList()
    println("Required parameters: $requiredParams")
    println("Parameter 'a' is required: ${"a" in requiredParams}")
}