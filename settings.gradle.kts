pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "mcp-ksp"
include("core")
include("mcp")
include("mcp-ksp")
include("mcp-ksp-test")
include("api-google-gemini")
include("api-langchain4j")
include("api-springai")
include("api-openai")
