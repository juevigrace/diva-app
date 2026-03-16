plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(libs.koin.android)
        }

        commonMain.dependencies {
            api(projects.core.database)

            api(projects.core.models)
            api(projects.core.ui)

            api(libs.diva.network.client)

            api(libs.koin.core)
        }
    }
}
