package io.github.stream29.langchain4kt2.mcp.ksp.test

import io.github.stream29.langchain4kt2.mcp.McpServerComponent
import io.github.stream29.langchain4kt2.mcp.McpTool
import kotlinx.serialization.Serializable

@McpServerComponent
public class MyServerComponent {
    @McpTool
    public suspend fun response(message: String?): List<String> {
        return listOf(message!!)
    }
}