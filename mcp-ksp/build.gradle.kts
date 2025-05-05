plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    api(project(":mcp"))
    implementation(libs.ksp)
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
}