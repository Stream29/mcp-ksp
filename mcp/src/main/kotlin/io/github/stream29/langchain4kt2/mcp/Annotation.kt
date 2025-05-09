package io.github.stream29.langchain4kt2.mcp

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class McpServer

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public annotation class McpTool(
    public val name: String = "",
    public val description: String = "",
)
