package com.example.howtokotlin.model

import com.example.howtokotlin.model.id.CategoryId
import java.time.Instant

data class Category(
    val categoryId: CategoryId,
    val name: String,
    val description: String?,
    val created: Instant,
    val lastUpdated: Instant,
)
