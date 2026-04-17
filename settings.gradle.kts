pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "diva-app"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(
    ":apps:androidApp",
    ":apps:desktopApp",
)

include(":sharedUI")

include(
    ":core:models:models-core",
    ":core:models:models-api",
    ":core:models:models-shared",
)
include(":core:database")
include(":core:ui")

include(
    ":features:app:home",
    ":features:app:onboarding",
    ":features:app:services",
    ":features:app:library",
    ":features:app:feed",
    ":features:app:creation",
    ":features:app:profile",
)

include(
    ":features:auth:auth-core",
    ":features:auth:auth-shared",
    ":features:auth:forgot",
    ":features:auth:session",
    ":features:auth:signin",
    ":features:auth:signup",
)

include(":features:user")

include(":features:verification")
