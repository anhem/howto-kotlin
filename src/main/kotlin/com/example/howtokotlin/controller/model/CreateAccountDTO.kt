package com.example.howtokotlin.controller.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateAccountDTO(
    @field:Size(min = 6, max = 30)
    @field:NotNull
    val username: String,
    @field:Email
    @field:NotNull
    val email: String,
    @field:Size(min = 1, max = 100)
    val firstName: String? = null,
    @field:Size(min = 1, max = 100)
    val lastName: String? = null,
    @field:NotNull
    @field:Size(min = 1, max = 100)
    val password: String,
)