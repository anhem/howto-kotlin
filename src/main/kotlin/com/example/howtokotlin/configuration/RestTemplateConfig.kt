package com.example.howtokotlin.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {
    companion object {
        fun createSnakeCaseObjectMapper(objectMapper: ObjectMapper): ObjectMapper {
            return objectMapper.copy().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        }
    }

    @Bean
    fun urlHausRestTemplate(objectMapper: ObjectMapper): RestTemplate {
        val snakeCaseObjectMapper = createSnakeCaseObjectMapper(objectMapper)

        val mappingJackson2HttpMessageConverter = MappingJackson2HttpMessageConverter()
        mappingJackson2HttpMessageConverter.objectMapper = snakeCaseObjectMapper

        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(0, mappingJackson2HttpMessageConverter)

        return restTemplate
    }
}