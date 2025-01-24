package com.example.howtokotlin.configuration

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Validated
@Configuration
@ConfigurationProperties("howto")
data class HowtoConfig(
    @field:NotNull
    @field:Size(min = 10)
    var jwtSecret: String? = null,
    @field:NotNull
    @field:Valid
    var urlHaus: UrlHausConfig = UrlHausConfig(),
) {
    data class UrlHausConfig(
        @field:NotNull
        @field:Size(min = 1)
        var baseUrl: String? = null,
        @field:NotNull
        @field:Min(1)
        @field:Max(10)
        var maxAllowedUrls: Int = 0,
    )
}
