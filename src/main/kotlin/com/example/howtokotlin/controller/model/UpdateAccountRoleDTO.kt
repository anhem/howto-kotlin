package com.example.howtokotlin.controller.model

import com.example.howtokotlin.model.RoleName
import jakarta.validation.constraints.NotNull

data class UpdateAccountRoleDTO(
    @field:NotNull
    val role: RoleName,
)
