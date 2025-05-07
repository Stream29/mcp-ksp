package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlinx.serialization.Serializable

public fun isBoxNeeded(paramList: List<KSValueParameter>): Boolean {
    if (paramList.size != 1) return true
    val param = paramList.first()
    val type = param.type.resolve()
    if (type.isMarkedNullable) return true
    if (param.annotations.count() > 0) return true
    if (type.annotations.count() > 0) return true
    return false
}

public fun makeBox(boxClassName: String, paramList: List<KSValueParameter>): TypeSpec {
    return buildClassSpec(boxClassName) {
        addModifiers(KModifier.DATA, KModifier.INTERNAL)
        addAnnotation<Serializable>()
        val paramInfos = paramList.map { param ->
            val type = param.type.resolve()
            val name = param.name!!.asString()
            val propertyAnnotations = mutableListOf<KSAnnotation>()
            val typeAnnotations = mutableListOf<KSAnnotation>()
            typeAnnotations += type.annotations
            for (annotation in param.annotations) {
                val allowedUseSites = annotation.allowedUseSites()
                when {
                    allowedUseSites.contains(AnnotationTarget.PROPERTY) -> propertyAnnotations.add(annotation)
                    allowedUseSites.contains(AnnotationTarget.FIELD) -> propertyAnnotations.add(annotation)
                    allowedUseSites.contains(AnnotationTarget.TYPE) -> typeAnnotations.add(annotation)
                }
            }
            val typeName = type.toTypeName().copy(annotations = typeAnnotations.map { it.toAnnotationSpec() })
            ParamInfo(
                name = name,
                type = typeName,
                parameterAnnotations = propertyAnnotations,
                typeAnnotations = typeAnnotations
            )
        }
        primaryConstructor { paramInfos.forEach { addParameter(it.name, it.type) } }
        paramInfos.forEach { paramInfo ->
            addProperty(paramInfo.name, paramInfo.type) {
                initializer(paramInfo.name)
                addAnnotations(paramInfo.parameterAnnotations.map { it.toAnnotationSpec() })
            }
        }
    }
}

public data class ParamInfo(
    val name: String,
    val type: TypeName,
    val parameterAnnotations: List<KSAnnotation>,
    val typeAnnotations: List<KSAnnotation>,
)

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
    val allowedTargets = targetAnnotation.arguments.first().value as List<KSType>
    return allowedTargets.map { AnnotationTarget.valueOf(it.declaration.simpleName.asString()) }
}