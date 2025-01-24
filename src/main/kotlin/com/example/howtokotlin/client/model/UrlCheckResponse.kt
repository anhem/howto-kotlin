package com.example.howtokotlin.client.model

data class UrlCheckResponse(
    val queryStatus: String,
    val id: String?,
    val urlHausReference: String?,
    val url: String?,
    val urlStatus: String?,
    val host: String?,
    val threat: String?,
)