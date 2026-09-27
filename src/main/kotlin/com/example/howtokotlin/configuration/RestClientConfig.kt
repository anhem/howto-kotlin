package com.example.howtokotlin.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.web.client.RestClient
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.json.JsonMapper

@Configuration
class RestClientConfig {

    @Bean
    fun urlHausRestClientBuilder(): RestClient.Builder {
        val snakeCaseJsonMapper =
            JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build()

        return RestClient.builder()
            .configureMessageConverters { converters ->
                converters
                    .registerDefaults()
                    .withJsonConverter(JacksonJsonHttpMessageConverter(snakeCaseJsonMapper))
            }
    }

    @Bean
    fun urlHausRestClient(urlHausRestClientBuilder: RestClient.Builder): RestClient = urlHausRestClientBuilder.build()
}
