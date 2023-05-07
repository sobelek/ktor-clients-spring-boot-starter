package io.github.sobelek.ktorclientsspringbootstarter

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

@WireMockTest(httpPort = 8080)
class AutoConfigurationTest {

    private val contextRunner: ApplicationContextRunner =
        ApplicationContextRunner().withConfiguration(AutoConfigurations.of(KtorClientsAutoconfiguration::class.java))

    @Test
    fun createMultipleClients() {
        this.contextRunner
            .withPropertyValues(
                "http-clients.test-service.expectSuccess=true",
                "http-clients.test-service2.expectSuccess=true"
            ).run {
                val clients = it.getBeansOfType(HttpClient::class.java)
                assert(clients.size == 2)
            }
    }

    @ParameterizedTest
    @ValueSource(strings = ["true", "false"])
    fun shouldSetExpectSuccess(expectSuccess: String) {
        this.contextRunner
            .withPropertyValues(
                "http-clients.test-service.expectSuccess=${expectSuccess}"
            ).run {
                val client = it.getBean(HttpClient::class.java)
                val userConfig = getPrivateVariable(client, "userConfig")
                val expectSuccessValue = getPrivateVariable(userConfig, "expectSuccess")
                assert(expectSuccessValue == expectSuccess.toBoolean())

            }
    }

    @ParameterizedTest
    @ValueSource(strings = ["true", "false"])
    fun shouldSetFollowRedirects(followRedirects: String) {
        this.contextRunner
            .withPropertyValues(
                "http-clients.test-service.followRedirects=${followRedirects}"
            ).run {
                val client = it.getBean(HttpClient::class.java)
                val userConfig = getPrivateVariable(client, "userConfig")
                val followRedirectsValue = getPrivateVariable(userConfig, "followRedirects")
                assert(followRedirectsValue == followRedirects.toBoolean())

            }
    }

    @ParameterizedTest
    @ValueSource(strings = ["500", "1000"])
    fun shouldSetRequestTimeout(requestTimeout: String) {
        this.contextRunner
            .withPropertyValues(
                "http-clients.test-service.requestTimeout=${requestTimeout}"
            ).run {
                val client = it.getBean(HttpClient::class.java)

                stubFor(
                    get(urlEqualTo("/"))
                        .willReturn(
                            aResponse()
                                .withFixedDelay(requestTimeout.toInt())
                        )
                )

                runBlocking {
                    val exception = assertThrows<HttpRequestTimeoutException> {
                        client.get("http://localhost:8080/")
                    }
                    assert(exception.message!!.contains(Regex("Request timeout has expired .*${requestTimeout} ms")))
                }
            }
    }

    private fun getPrivateVariable(from: Any, variableName: String): Any {
        val c = from.javaClass.getDeclaredField(variableName)
        c.trySetAccessible()
        return c.get(from)
    }
}
