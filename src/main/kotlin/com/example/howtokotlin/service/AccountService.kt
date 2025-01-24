package com.example.howtokotlin.service

import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.AccountPassword
import com.example.howtokotlin.model.Role
import com.example.howtokotlin.model.RoleName.ADMINISTRATOR
import com.example.howtokotlin.model.RoleName.USER
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.AccountPasswordId.Companion.NEW_ACCOUNT_PASSWORD_ID
import com.example.howtokotlin.model.id.Password
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.repository.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountPasswordRepository: AccountPasswordRepository,
    private val accountRoleRepository: AccountRoleRepository,
    private val roleRepository: RoleRepository,
) {
    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun getUsers(): List<Account> = accountRepository.getAccounts()

    fun getAccount(accountId: AccountId): Account {
        require(!accountId.isNew()) { "invalid accountId" }
        return accountRepository.getAccount(accountId)
    }

    fun getAccounts(accountIds: Set<AccountId>): List<Account> {
        if (accountIds.isEmpty()) {
            return emptyList()
        }
        return accountRepository.getAccounts(accountIds)
    }

    @Transactional
    fun createUserAccount(
        account: Account,
        password: Password,
    ): AccountId {
        val userRole: Role = roleRepository.getRoleByName(USER)
        return createAccount(account, password, userRole)
    }

    @Transactional
    fun createAdministratorAccount(
        account: Account,
        password: Password,
    ): AccountId {
        val administratorRole: Role = roleRepository.getRoleByName(ADMINISTRATOR)
        return createAccount(account, password, administratorRole)
    }

    @Transactional
    fun removeAccount(accountId: AccountId) {
        require(!accountId.isNew()) { "invalid accountId" }
        val account: Account = accountRepository.getAccount(accountId)
        accountRoleRepository.removeRolesFromAccount(accountId)
        accountPasswordRepository.removePassword(accountId)
        accountRepository.removeAccount(accountId)
        log.info("Account {} removed", account)
    }

    private fun createAccount(
        account: Account,
        password: Password,
        userRole: Role,
    ): AccountId {
        require(account.accountId.isNew()) { "invalid accountId" }
        require(account.username != Username.UNKNOWN) { "reserved username" }
        require(!accountRepository.accountExists(account.username, account.email)) { "username or email already exists" }
        val accountId: AccountId = accountRepository.createAccount(account)
        accountRoleRepository.addRoleToAccount(accountId, userRole.roleId, Instant.now())
        val accountPassword =
            AccountPassword(
                accountPasswordId = NEW_ACCOUNT_PASSWORD_ID,
                accountId = accountId,
                password = password,
                created = Instant.now(),
            )
        accountPasswordRepository.createPassword(accountPassword)
        log.info("Account {} ({}) created", account.username, accountId)
        return accountId
    }
}
