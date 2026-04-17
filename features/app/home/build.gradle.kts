plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.user)

            implementation(projects.core.ui)

            implementation(projects.features.app.library)
            implementation(projects.features.app.feed)
            implementation(projects.features.app.creation)
            implementation(projects.features.app.profile)
        }
    }
}
