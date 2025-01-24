package com.example.howtokotlin.controller.model

data class AccountDTO(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
)