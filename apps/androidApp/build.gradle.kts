import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
    alias(libs.plugins.secrets.gradle)
}

dependencies {
    implementation(projects.sharedUI)
}

android {
    namespace = "com.diva.app"

    compileSdk {
        version = release(
            libs.versions.android.compileSdk
                .get()
                .toInt()
        )
    }

    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()

        applicationId = "com.diva.app"

        versionCode = libs.versions.app.version.code.get().toInt()
        versionName = libs.versions.app.version.name.get()

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources.excludes += "DebugProbesKt.bin"
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }

        all {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("$rootDir/build-logic/src/main/resources/proguard-rules.pro"),
            )
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("development")

        create("staging")

        create("production")

        all {
            dimension = "environment"
            applicationIdSuffix = ".$name"
            versionNameSuffix = "-$name"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "secrets.defaults.properties"
}
