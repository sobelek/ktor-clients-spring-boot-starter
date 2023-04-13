package io.github.sobelek.ktorclientsspringbootstarter

import io.ktor.client.HttpClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.runner.ApplicationContextRunner

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
}
