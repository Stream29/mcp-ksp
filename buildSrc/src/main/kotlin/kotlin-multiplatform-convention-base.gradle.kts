import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
}

kotlin {
    withSourcesJar()
    explicitApi()
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {}
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}