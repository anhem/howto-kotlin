package com.example.howtokotlin.model

import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import java.time.Instant

data class Reply(
    val replyId: ReplyId,
    val postId: PostId,
    val accountId: AccountId,
    val body: String,
    val created: Instant,
    val lastUpdated: Instant,
)
