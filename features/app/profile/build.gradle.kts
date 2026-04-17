plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.auth.session)
            implementation(projects.features.user)

            implementation(projects.core.ui)
        }
    }
}
