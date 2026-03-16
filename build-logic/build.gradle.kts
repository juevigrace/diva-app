plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.tools.gradle.plugin)
    implementation(libs.jetbrains.compose.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.kotlin.serialization.gradle.plugin)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

kotlin {
    jvmToolchain(21)
}
