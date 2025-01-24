package com.example.howtokotlin.model

import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.AccountPasswordId
import com.example.howtokotlin.model.id.Password
import java.time.Instant

data class AccountPassword(
    val accountPasswordId: AccountPasswordId,
    val accountId: AccountId,
    val password: Password,
    val created: Instant,
)
