plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
        }

        commonMain.dependencies {
            implementation(projects.core.ui)

            implementation(projects.core.models)

            implementation(libs.koin.core)
        }
    }
}
