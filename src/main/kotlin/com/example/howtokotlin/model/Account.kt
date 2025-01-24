package com.example.howtokotlin.model

import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import java.time.Instant

data class Account(
    val accountId: AccountId,
    val username: Username,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val created: Instant,
    val lastUpdated: Instant,
    val lastLogin: Instant? = null,
)
