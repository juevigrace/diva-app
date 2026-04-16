plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.user)

            implementation(projects.core.ui)
        }
    }
}