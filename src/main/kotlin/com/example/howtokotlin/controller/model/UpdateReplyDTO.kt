package com.example.howtokotlin.controller.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateReplyDTO(
    @field:NotNull
    @field:Size(min = 1)
    val body: String,
)
