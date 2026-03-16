import divabuild.internal.libs

plugins {
    id("divabuild.kmp-base")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
