plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.auth.authShared)
            implementation(projects.features.auth.forgot)
            implementation(projects.features.auth.session)
            implementation(projects.features.auth.signin)
            implementation(projects.features.auth.signup)
        }
    }
}
