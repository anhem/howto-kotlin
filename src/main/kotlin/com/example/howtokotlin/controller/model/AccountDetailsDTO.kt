package com.example.howtokotlin.controller.model

import java.time.Instant

data class AccountDetailsDTO(
    val account: AccountDTO,
    val created: Instant,
    val update: Instant,
    val roles: List<String>,
)