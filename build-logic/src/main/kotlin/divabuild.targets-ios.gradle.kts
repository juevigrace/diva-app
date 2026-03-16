plugins {
    id("divabuild.kmp-base")
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        iosMain.dependencies {}
    }
}
