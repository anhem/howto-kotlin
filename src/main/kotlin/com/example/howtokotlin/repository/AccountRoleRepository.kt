package com.example.howtokotlin.repository

import com.example.howtokotlin.model.Role
import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.RoleId
import com.example.howtokotlin.repository.mapper.RoleMapper.mapToRole
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant

@Repository
class AccountRoleRepository(
    namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : JdbcRepository(namedParameterJdbcTemplate) {
    companion object {
        private const val SELECT_ROLES_FOR_ACCOUNT =
            "SELECT * FROM role r JOIN account_role ar ON r.role_id = ar.role_id WHERE ar.account_id = :accountId"
        private const val INSERT_ACCOUNT_ROLE = "INSERT INTO account_role(account_id, role_id, created) VALUES (:accountId, :roleId, :created)"
        private const val DELETE_ACCOUNT_ROLES = "DELETE FROM account_role WHERE account_id = :accountId"
    }

    fun getRoleNames(accountId: AccountId): List<RoleName> {
        val parameters = createParameters("accountId", accountId.value)
        return namedParameterJdbcTemplate
            .query(SELECT_ROLES_FOR_ACCOUNT, parameters) { rs, _ -> mapToRole(rs) }
            .map { role: Role -> role.roleName }
    }

    fun addRoleToAccount(
        accountId: AccountId,
        roleId: RoleId,
        created: Instant,
    ): Boolean {
        val parameters =
            createParameters("accountId", accountId.value)
                .addValue("roleId", roleId.value)
                .addValue("created", Timestamp.from(created))
        namedParameterJdbcTemplate.update(INSERT_ACCOUNT_ROLE, parameters)
        log.info("{} added to {}", roleId, accountId)
        return true
    }

    fun removeRolesFromAccount(accountId: AccountId): Boolean {
        val parameters = createParameters("accountId", accountId.value)
        namedParameterJdbcTemplate.update(DELETE_ACCOUNT_ROLES, parameters)
        log.info("All roles removed from {}", accountId)
        return true
    }
}
