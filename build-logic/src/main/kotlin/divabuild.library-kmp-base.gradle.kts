import divabuild.internal.libs

plugins {
    id("divabuild.app-package")
    id("divabuild.kmp-base")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.diva.core)
        }
    }
}
