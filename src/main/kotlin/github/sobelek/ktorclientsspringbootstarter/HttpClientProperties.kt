package github.sobelek.ktorclientsspringbootstarter

import io.ktor.client.plugins.logging.LogLevel

data class HttpClientProperties (
    val expectSuccess: Boolean = false,
    val followRedirects: Boolean = false,
    val requestTimeout: Long?,
    val connectionTimeout: Long?,
    val socketTimeout: Long?,
    val retryOn: RetryOn?,
    val maxRetries: Int = 3,
    val logLevel: LogLevel = LogLevel.NONE
    )

enum class RetryOn {SERVER_ERROR, EXCEPTION, SERVER_ERROR_OR_EXCEPTION}