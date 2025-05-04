plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    api(libs.mcp.sdk)
    api(libs.json.schema.generator)
    api(libs.kotlinx.coroutine.core)
    api(libs.kotlinx.serialization.core)
    testImplementation(libs.kotlin.test)
}