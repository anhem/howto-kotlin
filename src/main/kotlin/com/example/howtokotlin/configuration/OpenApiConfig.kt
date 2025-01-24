package com.example.howtokotlin.configuration

import com.example.howtokotlin.configuration.JwtTokenFilter.Companion.BEARER_AUTHENTICATION
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(name = BEARER_AUTHENTICATION, type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
class OpenApiConfig
