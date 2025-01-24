package com.example.howtokotlin.controller.model

import java.time.Instant

data class PostDTO(
    val postId: Int,
    val categoryId: Int,
    val username: String,
    val title: String,
    val body: String,
    val created: Instant,
    val updated: Instant,
)
