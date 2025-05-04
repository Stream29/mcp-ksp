plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

dependencies {
    api(project(":mcp"))
    ksp(project(":mcp-ksp"))
    testImplementation(libs.kotlin.test)
}