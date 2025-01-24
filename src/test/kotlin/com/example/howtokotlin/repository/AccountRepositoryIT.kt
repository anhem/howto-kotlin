package com.example.howtokotlin.repository

import com.example.howtokotlin.exception.NotFoundException
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.testutil.TestApplication
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.util.*

internal class AccountRepositoryIT : TestApplication() {
    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Test
    fun crd() {
        val account =
            Account(
                accountId = AccountId.NEW_ACCOUNT_ID,
                username = Username(UUID.randomUUID().toString().substring(0, 30)),
                email = "test@example.com",
                firstName = "newFirst",
                lastName = "newLast",
                created = Instant.now(),
                lastUpdated = Instant.now(),
            )

        val accountId: AccountId = accountRepository.createAccount(account)

        val foundAccount: Account? = findAccount(accountId)
        assertThat(foundAccount).isNotNull
        assertAccount(foundAccount!!, account, accountId)
        assertAccount(accountRepository.getAccount(account.username), account, accountId)
        assertAccount(accountRepository.getAccount(accountId), account, accountId)
        assertThat(accountRepository.accountExists(account.username, "")).isTrue()
        assertThat(accountRepository.accountExists(Username(""), account.email)).isTrue()

        accountRepository.removeAccount(accountId)

        assertThat(findAccount(accountId)).isNull()
        assertThatThrownBy { accountRepository.getAccount(account.username) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessageContaining(account.username.toString())
        assertThatThrownBy { accountRepository.getAccount(accountId) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessageContaining(accountId.toString())
        assertThat(accountRepository.accountExists(account.username, "")).isFalse()
        assertThat(accountRepository.accountExists(Username(""), account.email)).isFalse()
    }

    private fun findAccount(accountId: AccountId): Account? {
        val accountFromAllAccounts: Account? = accountRepository.getAccounts().firstOrNull { it.accountId == accountId }
        val accountFromSetOfAccountIds: Account? = accountRepository.getAccounts(setOf(accountId)).firstOrNull { it.accountId == accountId }

        assertThat(accountFromAllAccounts).isEqualTo(accountFromSetOfAccountIds)

        return accountFromAllAccounts
    }

    companion object {
        private fun assertAccount(
            readAccount: Account,
            account: Account,
            accountId: AccountId,
        ) {
            assertThat(readAccount)
                .usingRecursiveComparison()
                .ignoringFields("accountId", "created", "lastUpdated")
                .isEqualTo(account)
            assertThat(readAccount.accountId).isEqualTo(accountId)
            assertThat(readAccount.created).isBetween(account.created.minusSeconds(1), account.created.plusSeconds(1))
            assertThat(readAccount.lastUpdated).isBetween(account.lastUpdated.minusSeconds(1), account.lastUpdated.plusSeconds(1))
        }
    }
}
