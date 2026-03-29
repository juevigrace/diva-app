plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.database)

            api(projects.core.ui)

            api(libs.diva.network.client)
        }
    }
}
