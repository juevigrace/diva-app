plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.database)

            implementation(projects.core.ui)

            implementation(projects.features.auth.forgot)

            implementation(projects.features.user)

            implementation(libs.diva.network.client)
        }
    }
}
