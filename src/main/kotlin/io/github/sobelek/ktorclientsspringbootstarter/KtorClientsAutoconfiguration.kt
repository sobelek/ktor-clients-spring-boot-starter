package io.github.sobelek.ktorclientsspringbootstarter

import io.ktor.client.HttpClient
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.BindResult
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment

@AutoConfiguration
class KtorClientsAutoconfiguration {
    @Bean(name = ["KtorClientFactory"])
    fun ktorClientFactory(): KtorClientFactory {
        return KtorClientFactory()
    }

    @Bean
    fun beanPostProcessor(environment: ConfigurableEnvironment): BeanDefinitionRegistryPostProcessor? {
        return HttpClientRegisteringBeanDefinitionRegistryPostProcessor(environment)
    }

    private class HttpClientRegisteringBeanDefinitionRegistryPostProcessor
        (private val environment: ConfigurableEnvironment) : BeanDefinitionRegistryPostProcessor {
        override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        }

        override fun postProcessBeanDefinitionRegistry(beanRegistry: BeanDefinitionRegistry) {
            val clientConnectionPropertiesMapBindResult: BindResult<Map<String, HttpClientProperties>> = Binder.get(
                environment
            )
                .bind(
                    HTTP_CLIENTS, Bindable.mapOf(
                        String::class.java,
                        HttpClientProperties::class.java
                    )
                )
            if (clientConnectionPropertiesMapBindResult.isBound) {
                val httpClientPropertiesMap: Map<String, HttpClientProperties> =
                    clientConnectionPropertiesMapBindResult.get()
                httpClientPropertiesMap.forEach { (key: String) ->
                    val beanDefinition = getBeanDefinition(key)
                    beanRegistry.registerBeanDefinition("http-client-$key", beanDefinition)
                }
            }
        }

        private fun getBeanDefinition(key: String): BeanDefinition {
            val properties = Binder.get(environment).bind(buildName(key), HttpClientProperties::class.java).get()
            return BeanDefinitionBuilder.genericBeanDefinition(HttpClient::class.java)
                .addConstructorArgValue(properties)
                .setFactoryMethodOnBean("createKtorClient", "KtorClientFactory")
                .beanDefinition
        }

        private fun buildName(key: String): String {
            return "$HTTP_CLIENTS.$key"
        }

        companion object {
            private const val HTTP_CLIENTS = "http-clients"
        }
    }
}