plugins {
    id("divabuild.library")
    id("divabuild.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.database)

            implementation(projects.features.auth.authShared)
            implementation(projects.features.auth.session)
            implementation(projects.features.user)

            implementation(libs.diva.network.client)
        }
    }
}
