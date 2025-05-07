plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
    id("publish-conventions")
}

dependencies {
    api(project(":mcp"))
    implementation(libs.ksp)
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
}

publishing {
    publications.find { it.name == "maven" }?.let {
        val publication = it as MavenPublication
        publication.artifactId = project.name
    }
}