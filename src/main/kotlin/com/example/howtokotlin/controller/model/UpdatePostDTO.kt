package com.example.howtokotlin.controller.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdatePostDTO(
    @field:NotNull
    @field:Size(min = 1, max = 100)
    val title: String,
    @field:NotNull
    @field:Size(min = 1)
    val body: String,
)
