package com.example.howtokotlin.controller.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpsertCategoryDTO(
    @field:NotNull
    @field:Size(min = 1, max = 100)
    val name: String,
    @field:Size(max = 320)
    val description: String,
)
