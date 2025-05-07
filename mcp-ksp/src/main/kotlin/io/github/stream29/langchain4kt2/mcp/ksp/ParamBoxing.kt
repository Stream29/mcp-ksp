package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
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

public val McpToolFunctionInfo.boxClassName: String
    get() = "${className}$${functionName}\$Box"

public val McpToolFunctionInfo.boxFunctionName: String
    get() = "${functionName}\$boxed"

context(file: FileSpec.Builder)
public fun McpToolFunctionInfo.makeBox() {
    file.addClass(boxClassName) {
        addModifiers(KModifier.DATA, KModifier.INTERNAL)
        addAnnotation<Serializable>()
        val paramInfos = paramInfos.map { it.declaration }.map { it.parseParamInfo() }
        primaryConstructor { paramInfos.forEach { addParameter(it.name, it.type) } }
        paramInfos.forEach { paramInfo ->
            addProperty(paramInfo.name, paramInfo.type) {
                initializer(paramInfo.name)
                addAnnotations(paramInfo.parameterAnnotations.map { it.toAnnotationSpec() })
            }
        }
    }
    file.addFunction(boxFunctionName) {
        receiver(classDeclaration.toClassName())
        returns(funcDeclaration.returnType!!.toTypeName())
        addModifiers(KModifier.SUSPEND, KModifier.INTERNAL)
        addParameter("box", ClassName(classDeclaration.packageName.asString(), boxClassName))
        addCode {
            add("return $functionName(\n⇥⇥")
            paramInfos.forEach { paramInfo ->
                add("${paramInfo.name} = box.${paramInfo.name},\n")
            }
            add("⇤⇤)\n")
        }
    }
}