package com.diva.app.di.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.diva.database.DivaDB
import io.github.juevigrace.diva.database.DivaDatabase
import io.github.juevigrace.diva.database.driver.DriverProvider
import io.github.juevigrace.diva.database.driver.Schema
import migrations.Diva_session
import migrations.Diva_user
import migrations.Diva_user_pending_actions
import migrations.Diva_user_preferences
import org.koin.core.module.Module
import org.koin.dsl.module

fun databaseModule(): Module {
    return module {
        includes(driverModule())

        single<SqlDriver> {
            val provider: DriverProvider = get()
            // TODO: find a way to not throw
            provider.createDriver(Schema.Async(DivaDB.Schema)).getOrThrow()
        }

        single<DivaDatabase<DivaDB>> {
            val driver: SqlDriver = get()
            DivaDatabase(
                driver = driver,
                db = DivaDB(
                    driver = driver,
                    diva_sessionAdapter = Diva_session.Adapter(
                        statusAdapter = EnumColumnAdapter(),
                        typeAdapter = EnumColumnAdapter(),
                    ),
                    diva_userAdapter = Diva_user.Adapter(
                        roleAdapter = EnumColumnAdapter(),
                    ),
                    diva_user_preferencesAdapter = Diva_user_preferences.Adapter(
                        themeAdapter = EnumColumnAdapter(),
                    ),
                    diva_user_pending_actionsAdapter = Diva_user_pending_actions.Adapter(
                        action_nameAdapter = EnumColumnAdapter(),
                    ),
                ),
            )
        }
    }
}
