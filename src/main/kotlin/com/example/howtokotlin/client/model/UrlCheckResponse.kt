package com.example.howtokotlin.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class UrlCheckResponse(
    val queryStatus: String,
    val id: String?,
    @param:JsonProperty("urlhaus_reference")
    val urlHausReference: String?,
    val url: String?,
    val urlStatus: String?,
    val host: String?,
    val threat: String?,
)