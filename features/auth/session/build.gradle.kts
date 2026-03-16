plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.auth.authShared)
        }
    }
}
