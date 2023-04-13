package io.github.sobelek.ktorclientsspringbootstarter

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.*

class KtorClientFactory() {

    fun createKtorClient(properties: HttpClientProperties): HttpClient {
        print(properties)
        return HttpClient(CIO) {
            expectSuccess = properties.expectSuccess
            followRedirects = properties.followRedirects
            install(HttpTimeout) {
                requestTimeoutMillis = properties.requestTimeout
                socketTimeoutMillis = properties.socketTimeout
                connectTimeoutMillis = properties.connectionTimeout
            }
            install(Logging) {
                level = properties.logLevel
            }
            install(HttpRequestRetry) {
                maxRetries = properties.maxRetries
                when (properties.retryOn) {
                    RetryOn.SERVER_ERROR -> retryOnServerErrors()
                    RetryOn.EXCEPTION -> retryOnException()
                    RetryOn.SERVER_ERROR_OR_EXCEPTION -> retryOnExceptionOrServerErrors()
                    null -> noRetry()
                }
            }
        }
    }
}