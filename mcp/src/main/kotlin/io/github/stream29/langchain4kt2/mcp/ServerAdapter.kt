package io.github.stream29.langchain4kt2.mcp

import io.github.stream29.jsonschemagenerator.SchemaGenerator
import io.github.stream29.jsonschemagenerator.schemaOf
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*

public class ServerAdapter(
    public val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    },
    public val schemaGenerator: SchemaGenerator = SchemaGenerator.default
) {
    public companion object {
        public val default: ServerAdapter = ServerAdapter()
    }
}

public inline fun <T : Any, R> ServerAdapter.makeToolUnsafe(
    name: String,
    description: String?,
    paramSchema: JsonObject,
    paramSerializer: KSerializer<T>,
    crossinline body: suspend (T) -> R,
    crossinline mapReturn: (R) -> String,
): RegisteredTool {
    return RegisteredTool(
        Tool(
            name = name,
            description = description,
            inputSchema = Tool.Input(
                properties = paramSchema["properties"]!!.jsonObject,
                required = paramSchema["required"]?.jsonArray?.map { it.jsonPrimitive.content },
            )
        )
    ) handler@{ (_, arguments, _) ->
        try {
            val param = json.decodeFromJsonElement(paramSerializer, arguments)
            CallToolResult.ok(mapReturn(body(param)))
        } catch (e: Throwable) {
            CallToolResult.error(e.toString())
        }
    }
}