package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.*

public fun KSTypeReference.qualifiedName(): String? = this.resolve().declaration.qualifiedName?.asString()

public inline fun buildFileSpec(
    packageName: String,
    fileName: String,
    builder: FileSpec.Builder.() -> Unit
): FileSpec = FileSpec.builder(packageName, fileName).apply(builder).build()

public inline fun FileSpec.Builder.addFunction(
    name: String,
    buildAction: FunSpec.Builder.() -> Unit
) {
    addFunction(buildFunSpec(name, buildAction))
}

public inline fun FileSpec.Builder.addClass(
    name: String,
    buildAction: TypeSpec.Builder.() -> Unit
) {
    addType(buildClassSpec(name, buildAction))
}

public inline fun FunSpec.Builder.addCode(buildAction: CodeBlock.Builder.() -> Unit) {
    addCode(CodeBlock.builder().apply(buildAction).build())
}

public inline fun buildFunSpec(name: String, builder: FunSpec.Builder.() -> Unit): FunSpec =
    FunSpec.builder(name).apply(builder).build()

public inline fun buildClassSpec(
    name: String,
    builder: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.classBuilder(name).apply(builder).build()

public inline fun <reified T> TypeSpec.Builder.addAnnotation() {
    addAnnotation(T::class)
}

public inline fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    buildAction: PropertySpec.Builder.() -> Unit = {}
) {
    addProperty(
        PropertySpec.builder(name, type).apply(buildAction).build()
    )
}

public inline fun TypeSpec.Builder.primaryConstructor(
    buildAction: FunSpec.Builder.() -> Unit
) {
    primaryConstructor(
        FunSpec.constructorBuilder().apply(buildAction).build()
    )
}

public inline fun <reified T> FunSpec.Builder.returns() {
    returns(typeNameOf<T>())
}

public inline fun <reified T> FunSpec.Builder.addParameter(
    name: String,
    buildAction: ParameterSpec.Builder.() -> Unit = {}
) {
    addParameter(
        ParameterSpec.builder(name, typeNameOf<T>()).apply(buildAction).build()
    )
}