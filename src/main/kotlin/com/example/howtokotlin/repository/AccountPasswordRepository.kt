package com.example.howtokotlin.repository

import com.example.howtokotlin.exception.NotFoundException
import com.example.howtokotlin.model.AccountPassword
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.AccountPasswordId
import com.example.howtokotlin.repository.mapper.AccountPasswordMapper.mapToAccountPassword
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
class AccountPasswordRepository(
    namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : JdbcRepository(
        namedParameterJdbcTemplate,
    ) {
    companion object {
        private const val SELECT_PASSWORD = "SELECT * FROM account_password WHERE account_id = :id"
        private const val INSERT_PASSWORD = "INSERT INTO account_password(account_id, password, created) VALUES(:accountId, :password, :created)"
        private const val DELETE_PASSWORD = "DELETE FROM account_password where account_id = :accountId"
    }

    fun getPassword(accountId: AccountId): AccountPassword {
        return findById(accountId, SELECT_PASSWORD) { rs, _ -> mapToAccountPassword(rs) }
    }

    fun createPassword(accountPassword: AccountPassword): AccountPasswordId {
        val parameters =
            createParameters("accountId", accountPassword.accountId.value)
                .addValue("password", accountPassword.password.value)
                .addValue("created", Timestamp.from(accountPassword.created))
        return insert(INSERT_PASSWORD, parameters, AccountPasswordId::class.java)
    }

    fun removePassword(accountId: AccountId) {
        val parameters = createParameters("accountId", accountId.value)
        namedParameterJdbcTemplate.update(DELETE_PASSWORD, parameters)
    }
}
