plugins {
    id("divabuild.library-ui")
    id("divabuild.serialization")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
        }

        commonMain.dependencies {
            implementation(projects.core.database)

            implementation(projects.core.models)
            implementation(projects.core.ui)

            implementation(libs.diva.network.client)

            implementation(libs.koin.core)
        }
    }
}
