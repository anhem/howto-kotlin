package com.example.howtokotlin.controller.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateReplyDTO(
    @field:NotNull
    val postId: Int,
    @field:NotNull
    @field:Size(min = 1)
    val body: String,
)
