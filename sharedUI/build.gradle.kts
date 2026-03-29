plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.database)
            implementation(projects.core.ui)

            implementation(projects.features.app.home)
            implementation(projects.features.app.onboarding)

            implementation(projects.features.auth.authCore)

            implementation(projects.features.user)

            implementation(projects.features.verification)

            implementation(libs.diva.network.client)
        }

        jvmMain.dependencies {
            api(libs.koin.logger.slf4j)
        }
    }
}
