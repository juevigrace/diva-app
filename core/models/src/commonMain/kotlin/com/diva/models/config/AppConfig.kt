package com.diva.models.config

import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.config.DivaAppConfig
import io.github.juevigrace.diva.core.config.Environment
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.core.map

data class AppConfig(
    override val debug: Boolean = true,
    override val environment: Environment = Environment.DEVELOPMENT,
    override val domain: String = "localhost",
    override val port: Option<Int> = Option.Some(5000),
    val protocol: String = if (environment == Environment.DEVELOPMENT) {
        "http"
    } else {
        "https"
    },
    override val version: String = "1.0",
    val versionName: String = "$version${if (environment != Environment.PRODUCTION) {
        "-$environment"
    } else {
        ""
    }
    }${if (debug) {
        "-debug"
    } else {
        ""
    }}",
    override val deviceName: String = "Unknown",
    override val agent: String = "Diva/$version (Unknown)",
    override val baseUrl: String = "$protocol://$domain${port.map { ":$it" }.getOrElse { "" }}",
) : DivaAppConfig
