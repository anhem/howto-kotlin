package com.example.howtokotlin.repository.mapper

import com.example.howtokotlin.model.Role
import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.model.id.RoleId
import java.sql.ResultSet

object RoleMapper {
    fun mapToRole(rs: ResultSet): Role =
        Role(
            roleId = RoleId(rs.getInt("role_id")),
            roleName = RoleName.fromName(rs.getString("role_name")),
            description = rs.getString("description"),
            created = rs.getTimestamp("created").toInstant(),
            lastUpdated = rs.getTimestamp("last_updated").toInstant(),
        )
}
