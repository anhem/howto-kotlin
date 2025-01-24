package com.example.howtokotlin.model.id

data class JwtToken(
    override val value: String,
) : Id<String>
