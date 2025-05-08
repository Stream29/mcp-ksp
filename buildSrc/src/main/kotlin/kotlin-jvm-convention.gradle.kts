plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

java {
    withSourcesJar()
}