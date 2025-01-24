package com.example.howtokotlin.controller.model

data class ErrorDTO(
    val errorCode: ErrorCode,
    val message: String?,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
)
