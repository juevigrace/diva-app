plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.models)
            implementation(projects.core.database)
            implementation(projects.core.ui)

            implementation(projects.features.app.home)
            implementation(projects.features.app.onboarding)

            implementation(projects.features.auth.authCore)

            implementation(projects.features.user)

            implementation(libs.diva.network.client)
            api(libs.koin.core)
        }

        androidMain.dependencies {
            api(libs.koin.android)
        }

        jvmMain.dependencies {
            api(libs.koin.logger.slf4j)
        }
    }
}
