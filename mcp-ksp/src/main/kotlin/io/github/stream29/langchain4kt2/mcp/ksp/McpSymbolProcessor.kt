package io.github.stream29.langchain4kt2.mcp.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.stream29.langchain4kt2.mcp.McpServer
import io.github.stream29.langchain4kt2.mcp.ServerAdapter
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool

public class McpSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(McpServer::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>().forEach { ksClassDeclaration ->
                val mcpToolList = ksClassDeclaration.getAllFunctions()
                    .filter { it.isMcpTool() }
                val mcpToolInfoList = mcpToolList.map { it.parseMcpToolInfo() }
                val makeTool = MemberName(
                    "io.github.stream29.langchain4kt2.mcp",
                    "makeTool"
                )
                val fileSpec = buildFileSpec(
                    ksClassDeclaration.packageName.asString(),
                    "Generated$${ksClassDeclaration.simpleName.asString()}"
                ) {
                    addFunction("tools") {
                        receiver(ksClassDeclaration.toClassName())
                        returns<List<RegisteredTool>>()
                        addParameter<ServerAdapter>("adapter") {
                            defaultValue("%T()", ServerAdapter::class)
                        }
                        addCode {
                            add("return listOf(\n⇥⇥")
                            mcpToolInfoList.forEach { mcpTool ->
                                val functionName = if(mcpTool.isBoxNeeded) {
                                    mcpTool.boxFunctionName
                                } else {
                                    mcpTool.functionName
                                }
                                add(
                                    "adapter.%M(\n⇥⇥\"${mcpTool.functionName}\", \nnull, \nthis::`${functionName}`\n⇤⇤),\n",
                                    makeTool
                                )
                            }
                            add("⇤⇤)")
                        }
                    }
                    mcpToolInfoList.asSequence()
                        .filter { it.isBoxNeeded }
                        .forEach { it.makeBox() }
                }
                fileSpec.writeTo(environment.codeGenerator, Dependencies(false))
            }
        return emptyList()
    }
}