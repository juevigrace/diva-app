package com.diva.app.di.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.diva.database.DivaDB
import io.github.juevigrace.diva.core.getOrThrow
import io.github.juevigrace.diva.database.DivaDatabase
import io.github.juevigrace.diva.database.driver.DriverProvider
import io.github.juevigrace.diva.database.driver.Schema
import migrations.Diva_chat
import migrations.Diva_chat_participant
import migrations.Diva_collection
import migrations.Diva_media
import migrations.Diva_message
import migrations.Diva_permissions
import migrations.Diva_playlist_suggestions
import migrations.Diva_post
import migrations.Diva_session
import migrations.Diva_user
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
                    diva_chatAdapter = Diva_chat.Adapter(
                        chat_typeAdapter = EnumColumnAdapter(),
                    ),
                    diva_chat_participantAdapter = Diva_chat_participant.Adapter(
                        roleAdapter = EnumColumnAdapter(),
                    ),
                    diva_collectionAdapter = Diva_collection.Adapter(
                        collection_typeAdapter = EnumColumnAdapter(),
                        visibilityAdapter = EnumColumnAdapter(),
                    ),
                    diva_mediaAdapter = Diva_media.Adapter(
                        media_typeAdapter = EnumColumnAdapter(),
                        visibilityAdapter = EnumColumnAdapter(),
                    ),
                    diva_messageAdapter = Diva_message.Adapter(
                        message_typeAdapter = EnumColumnAdapter(),
                    ),
                    diva_permissionsAdapter = Diva_permissions.Adapter(
                        role_levelAdapter = EnumColumnAdapter(),
                    ),
                    diva_playlist_suggestionsAdapter = Diva_playlist_suggestions.Adapter(
                        statusAdapter = EnumColumnAdapter(),
                    ),
                    diva_postAdapter = Diva_post.Adapter(
                        visibilityAdapter = EnumColumnAdapter(),
                    ),
                    diva_sessionAdapter = Diva_session.Adapter(
                        statusAdapter = EnumColumnAdapter(),
                    ),
                    diva_userAdapter = Diva_user.Adapter(
                        roleAdapter = EnumColumnAdapter(),
                    ),
                    diva_user_preferencesAdapter = Diva_user_preferences.Adapter(
                        typeAdapter = EnumColumnAdapter(),
                        themeAdapter = EnumColumnAdapter(),
                    ),
                ),
            )
        }
    }
}
