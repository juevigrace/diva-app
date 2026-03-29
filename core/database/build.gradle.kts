plugins {
    id("divabuild.library")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.models.modelsCore)

            api(libs.diva.database)
        }
        jvmMain.dependencies {
            implementation(libs.sqlite)
        }
    }
}

sqldelight {
    databases {
        create("DivaDB") {
            packageName.set("com.diva.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            generateAsync.set(true)
            deriveSchemaFromMigrations.set(true)
            verifyMigrations.set(true)
        }
    }
}
