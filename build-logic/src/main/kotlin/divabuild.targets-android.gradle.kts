import divabuild.internal.cleanPath
import divabuild.internal.libs

plugins {
    id("divabuild.kmp-base")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    androidLibrary {
        namespace = "com.diva.${cleanPath()}"

        compileSdk = libs.versions.android.compileSdk.get().toInt()

        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }

        androidResources.enable = true
    }
}
