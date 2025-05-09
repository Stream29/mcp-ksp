package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.stream29.langchain4kt2.mcp.McpTool

public fun KSFunctionDeclaration.isMcpTool() =
    annotations.any { it.annotationType.qualifiedName() == McpTool::class.qualifiedName }

public fun KSFunctionDeclaration.parseMcpToolInfo(): McpToolFunctionInfo {
    val annotation = parseAnnotation()
    val functionName = this.simpleName.asString()
    val classDeclaration = this.parentDeclaration as KSClassDeclaration
    return McpToolFunctionInfo(
        name = annotation.name.ifEmpty { functionName },
        description = annotation.description.ifEmpty { null },
        funcDeclaration = this,
        classDeclaration = classDeclaration,
        className = classDeclaration.simpleName.asString(),
        functionName = functionName,
        annotations = this.annotations.toList(),
        params = this.parameters,
        paramInfos = parameters.map { it.parseParamInfo() },
        isBoxNeeded = isBoxNeeded(parameters),
    )
}

public fun KSFunctionDeclaration.parseAnnotation(): McpTool {
    val descriptionAnnotation = annotations.first { it.annotationType.qualifiedName() == McpTool::class.qualifiedName }
    val annotationParams = descriptionAnnotation.arguments.associateBy { it.name?.asString() }
    val name = annotationParams["name"]!!.value as String
    val description = annotationParams["description"]!!.value as String
    return McpTool(name, description)
}

public fun KSValueParameter.parseParamInfo(): ParamInfo {
    val type = this.type.resolve()
    val name = this.name!!.asString()
    val propertyAnnotations = mutableListOf<KSAnnotation>()
    val typeAnnotations = mutableListOf<KSAnnotation>()
    typeAnnotations += type.annotations
    for (annotation in this.annotations) {
        val allowedUseSites = annotation.allowedUseSites()
        when {
            allowedUseSites.contains(AnnotationTarget.PROPERTY) -> propertyAnnotations.add(annotation)
            allowedUseSites.contains(AnnotationTarget.FIELD) -> propertyAnnotations.add(annotation)
            allowedUseSites.contains(AnnotationTarget.TYPE) -> typeAnnotations.add(annotation)
        }
    }
    val typeName = type.toTypeName().copy(annotations = typeAnnotations.map { it.toAnnotationSpec() })
    return ParamInfo(
        declaration = this,
        name = name,
        type = typeName,
        parameterAnnotations = propertyAnnotations,
        typeAnnotations = typeAnnotations
    )
}

public fun KSAnnotation.allowedUseSites(): List<AnnotationTarget> {
    val declaration = annotationType.resolve().declaration
    val targetAnnotation = declaration.annotations
        .firstOrNull { it.annotationType.qualifiedName() == Target::class.qualifiedName }
        ?: return listOf(
            AnnotationTarget.CLASS,
            AnnotationTarget.PROPERTY,
            AnnotationTarget.FIELD,
            AnnotationTarget.LOCAL_VARIABLE,
            AnnotationTarget.VALUE_PARAMETER,
            AnnotationTarget.CONSTRUCTOR,
            AnnotationTarget.FUNCTION,
            AnnotationTarget.PROPERTY_GETTER,
            AnnotationTarget.PROPERTY_SETTER
        ) // See at https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.annotation/-target/

    @Suppress("UNCHECKED_CAST")
    val allowedTargets = targetAnnotation.arguments.first().value as List<KSType>
    return allowedTargets.map { AnnotationTarget.valueOf(it.declaration.simpleName.asString()) }
}

public data class McpToolFunctionInfo(
    val name: String,
    val description: String?,
    val classDeclaration: KSClassDeclaration,
    val className: String,
    val funcDeclaration: KSFunctionDeclaration,
    val functionName: String,
    val annotations: List<KSAnnotation>,
    val params: List<KSValueParameter>,
    val isBoxNeeded: Boolean,
    val paramInfos: List<ParamInfo>,
)

public data class ParamInfo(
    val declaration: KSValueParameter,
    val name: String,
    val type: TypeName,
    val parameterAnnotations: List<KSAnnotation>,
    val typeAnnotations: List<KSAnnotation>,
)
