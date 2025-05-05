package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

public fun makeTool(func: KSFunctionDeclaration): CodeBlock {
    return makeTool(
        declaration = func,
        annotations = func.annotations.toList(),
        parameters = func.parameters.map {
            McpParameter(
                name = it.name?.asString() ?: error("Error resolving fun ${func.simpleName.asString()} parameter name"),
                type = it.type.resolve(),
                annotations = it.annotations.toList(),
            )
        },
        returnType = func.returnType?.resolve()
            ?: error("Error resolving fun ${func.simpleName.asString()} return type"),
    )
}

public fun makeTool(
    declaration: KSFunctionDeclaration,
    annotations: List<KSAnnotation>,
    parameters: List<McpParameter>,
    returnType: KSType,
): CodeBlock {
    if (parameters.isEmpty()) {
        TODO()
    }
    if (parameters.any { it.annotations.isNotEmpty() }) {
        TODO()
    }
    if (parameters.any { it.type.isMarkedNullable }) {
        TODO()
    }
    if (parameters.size > 1) {
        TODO()
    }
    if (returnType.toClassName().reflectionName() != "kotlin.String") {
        TODO()
    }
    return makeToolUnboxed(
        declaration = declaration,
        annotations = annotations,
        parameters = parameters,
        returnType = returnType,
    )
}

public fun makeToolUnboxed(
    declaration: KSFunctionDeclaration,
    annotations: List<KSAnnotation>,
    parameters: List<McpParameter>,
    returnType: KSType,
): CodeBlock {
    return buildCodeBlock {
        add("adapter.%M(\n", MemberName("io.github.stream29.langchain4kt2.mcp", "makeToolUnsafe"))
        add("%S, \n", declaration.simpleName.asString())
        add("%S, \n", "no description")
        add("adapter.schemaGenerator.%M(♢", MemberName("io.github.stream29.jsonschemagenerator", "schemaOf"))
        add(serializerOf(parameters.first().type))
        add(".descriptor, ♢emptyList()♢), ♢")
        add(serializerOf(parameters.first().type))
        add(", ♢")
        add("::${declaration.simpleName.asString()}, ♢")
        add("{ it }♢")
        add(")")
    }
}

public fun serializerOf(
    type: KSType,
): CodeBlock = buildCodeBlock {
    add(
        "%M<%T>()",
        MemberName("kotlinx.serialization", "serializer"),
        type.toTypeName()
    )
}

public data class McpParameter(
    val name: String,
    val type: KSType,
    val annotations: List<KSAnnotation>,
)