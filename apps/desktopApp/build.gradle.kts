import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.hot.reload)
}

dependencies {
    implementation(projects.sharedUI)

    implementation(compose.desktop.currentOs)
}

compose {
    desktop {
        application {
            mainClass = "com.diva.app.MainKt"

            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "com.diva.app"
                packageVersion = libs.versions.app.version.name.get()
            }
        }
    }
}
