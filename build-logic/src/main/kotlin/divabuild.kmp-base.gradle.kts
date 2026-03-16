import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_3
        languageVersion = KotlinVersion.KOTLIN_2_3
        progressiveMode = languageVersion.map { it >= KotlinVersion.DEFAULT }
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}
