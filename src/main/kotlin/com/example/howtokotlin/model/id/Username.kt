package com.example.howtokotlin.model.id

data class Username(
    override val value: String,
) : Id<String> {
    companion object {
        val UNKNOWN: Username = Username("unknown")
    }
}
