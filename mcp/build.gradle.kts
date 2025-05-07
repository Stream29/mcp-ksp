plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
    id("publish-conventions")
}

dependencies {
    api(libs.mcp.sdk)
    api(libs.json.schema.generator)
    api(libs.kotlinx.coroutine.core)
    api(libs.kotlinx.serialization.core)
    testImplementation(libs.kotlin.test)
}

publishing {
    publications.find { it.name == "maven" }?.let {
        val publication = it as MavenPublication
        publication.artifactId = "mcp-utils"
    }
}