import divabuild.internal.libs

plugins {
    id("divabuild.library-kmp-base")
    id("divabuild.library-targets")
    id("divabuild.setup-compose-multiplatform")
    id("divabuild.serialization")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(libs.koin.android)
            api(libs.koin.androidx.compose)
        }

        commonMain.dependencies {
            api(libs.diva.ui)

            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
        }
    }
}

compose.resources {
    generateResClass = never
    publicResClass = false
}
