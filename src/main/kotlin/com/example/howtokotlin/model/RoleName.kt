package com.example.howtokotlin.model

import com.example.howtokotlin.model.id.Id

enum class RoleName(
    val role: String,
) : Id<String> {
    USER(Constants.USER),
    MODERATOR(Constants.MODERATOR),
    ADMINISTRATOR(Constants.ADMINISTRATOR),
    ;

    companion object {
        fun fromName(name: String): RoleName =
            entries.find { it.name == name }
                ?: throw RuntimeException("Invalid roleName $name")

        fun fromRole(role: String): RoleName {
            val value = role.replace("ROLE_", "")
            return entries.find { it.name == value }
                ?: throw RuntimeException("Invalid value $role")
        }
    }

    object Constants {
        const val USER = "ROLE_USER"
        const val MODERATOR = "ROLE_MODERATOR"
        const val ADMINISTRATOR = "ROLE_ADMINISTRATOR"
    }

    override val value: String get() = this.name
}
