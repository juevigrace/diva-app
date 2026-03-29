plugins {
    id("divabuild.library-ui")
    id("divabuild.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.database)

            implementation(projects.core.ui)

            implementation(libs.diva.network.client)
        }
    }
}
