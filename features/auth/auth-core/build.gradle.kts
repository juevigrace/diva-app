plugins {
    id("divabuild.library-ui")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.features.auth.authShared)
            implementation(projects.features.auth.forgot)
            api(projects.features.auth.session)
            implementation(projects.features.auth.signin)
            implementation(projects.features.auth.signup)
        }
    }
}
