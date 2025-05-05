package io.github.stream29.langchain4kt2.mcp.ksp.test

import io.github.stream29.langchain4kt2.mcp.McpServerComponent
import io.github.stream29.langchain4kt2.mcp.McpTool
import kotlinx.serialization.Serializable

@McpServerComponent
public class MyServerComponent {
    @McpTool
    public suspend fun response(message: Box<String>): String {
        return "Hello, ${message.value}"
    }
}

@Serializable
public data class Box<T>(val value: T)