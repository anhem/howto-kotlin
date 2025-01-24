package com.example.howtokotlin.model

import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import java.time.Instant

data class Post(
    val postId: PostId,
    val categoryId: CategoryId,
    val accountId: AccountId,
    val title: String,
    val body: String,
    val created: Instant,
    val lastUpdated: Instant,
)
