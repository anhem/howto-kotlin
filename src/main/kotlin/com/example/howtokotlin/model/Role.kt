package com.example.howtokotlin.model

import com.example.howtokotlin.model.id.RoleId
import java.time.Instant

data class Role(
    val roleId: RoleId,
    val roleName: RoleName,
    val description: String,
    val created: Instant,
    val lastUpdated: Instant,
)
