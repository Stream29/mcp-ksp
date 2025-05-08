plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    target.withSourcesJar()
}