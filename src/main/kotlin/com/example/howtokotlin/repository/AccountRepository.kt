package com.example.howtokotlin.repository

import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.repository.mapper.AccountMapper.mapToAccount
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
class AccountRepository(
    namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : JdbcRepository(namedParameterJdbcTemplate) {
    companion object {
        private const val SELECT_ACCOUNTS = "SELECT * FROM account"
        private const val SELECT_ACCOUNT_BY_ID = "SELECT * FROM account WHERE account_id = :id"
        private const val SELECT_ACCOUNTS_BY_IDS = "SELECT * FROM account WHERE account_id IN(:accountIds)"
        private const val SELECT_ACCOUNT_BY_USERNAME = "SELECT * FROM account WHERE username = :id"
        private const val INSERT_ACCOUNT =
            "INSERT INTO account(username, email, first_name, last_name, created, last_updated) values(:username, :email, :firstName, :lastName, :created, :lastUpdated)"
        private const val DELETE_ACCOUNT = "DELETE FROM account WHERE account_id = :accountId"
        private const val ACCOUNT_EXISTS = "SELECT EXISTS (SELECT 1 FROM account WHERE username = :username OR email =:email LIMIT 1)"

        private fun getAccountsIds(accountIds: Set<AccountId>): List<Int> = accountIds.map { accountId: AccountId -> accountId.value }
    }

    fun getAccounts(): List<Account> = namedParameterJdbcTemplate.query(SELECT_ACCOUNTS) { rs, _ -> mapToAccount(rs) }

    fun getAccounts(accountIds: Set<AccountId>): List<Account> {
        val accountsIds = getAccountsIds(accountIds)
        val parameters = createParameters("accountIds", accountsIds)

        return namedParameterJdbcTemplate.query(SELECT_ACCOUNTS_BY_IDS, parameters) { rs, _ -> mapToAccount(rs) }
    }

    fun getAccount(accountId: AccountId): Account = findById(accountId, SELECT_ACCOUNT_BY_ID) { rs, _ -> mapToAccount(rs) }

    fun getAccount(username: Username): Account = findById(username, SELECT_ACCOUNT_BY_USERNAME) { rs, _ -> mapToAccount(rs) }

    fun removeAccount(accountId: AccountId) {
        val parameters = createParameters("accountId", accountId.value)
        namedParameterJdbcTemplate.update(DELETE_ACCOUNT, parameters)
        log.info("Removed account {}", accountId)
    }

    fun createAccount(account: Account): AccountId {
        val parameters =
            createParameters("username", account.username.value)
                .addValue("email", account.email)
                .addValue("firstName", account.firstName)
                .addValue("lastName", account.lastName)
                .addValue("created", Timestamp.from(account.created))
                .addValue("lastUpdated", Timestamp.from(account.lastUpdated))
        return insert(INSERT_ACCOUNT, parameters, AccountId::class.java)
    }

    fun accountExists(
        username: Username,
        email: String,
    ): Boolean {
        val params = createParameters("username", username.value).addValue("email", email)
        return namedParameterJdbcTemplate.queryForObject(ACCOUNT_EXISTS, params, Boolean::class.java)!!
    }
}
