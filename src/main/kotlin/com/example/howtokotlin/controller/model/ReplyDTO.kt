package com.example.howtokotlin.controller.model

import java.time.Instant

data class ReplyDTO(
    val replyId: Int,
    val postId: Int,
    val username: String,
    val body: String,
    val created: Instant,
    val updated: Instant,
)