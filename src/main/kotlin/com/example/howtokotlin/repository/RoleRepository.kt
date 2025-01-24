package com.example.howtokotlin.repository

import com.example.howtokotlin.model.Role
import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.repository.mapper.RoleMapper.mapToRole
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class RoleRepository(
    namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : JdbcRepository(namedParameterJdbcTemplate) {
    companion object {
        private const val SELECT_ROLE_BY_NAME = "SELECT * FROM role WHERE role_name = :id"
    }

    fun getRoleByName(roleName: RoleName): Role {
        return findById(roleName, SELECT_ROLE_BY_NAME) { rs, _ -> mapToRole(rs) }
    }
}
