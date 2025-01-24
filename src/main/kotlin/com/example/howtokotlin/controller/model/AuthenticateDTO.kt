package com.example.howtokotlin.controller.model

import jakarta.validation.constraints.NotEmpty

data class AuthenticateDTO(
    @field:NotEmpty
    val username: String,
    @field:NotEmpty
    val password: String,
)
