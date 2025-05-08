package io.github.stream29.langchain4kt2.mcp.ksp.test

import io.github.stream29.jsonschemagenerator.Description
import io.github.stream29.langchain4kt2.mcp.McpServer
import io.github.stream29.langchain4kt2.mcp.McpTool

@McpServer
public class MyServerComponent {
    @McpTool(description = "Response as a list")
    public fun response(
        @Description("The first value in the list") message1: String,
        @Description("The second value in the list") message2: String,
    ): List<String> {
        return listOf(message1, message2)
    }
}