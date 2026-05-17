package com.example.howtokotlin.service

import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.AccountPassword
import com.example.howtokotlin.model.Role
import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.model.id.*
import com.example.howtokotlin.model.id.AccountPasswordId.Companion.NEW_ACCOUNT_PASSWORD_ID
import com.example.howtokotlin.repository.AccountPasswordRepository
import com.example.howtokotlin.repository.AccountRepository
import com.example.howtokotlin.repository.AccountRoleRepository
import com.example.howtokotlin.repository.RoleRepository
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
internal class AccountServiceTest {
    private val accountPasswordSlot = slot<AccountPassword>()

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var accountPasswordRepository: AccountPasswordRepository

    @MockK
    private lateinit var accountRoleRepository: AccountRoleRepository

    @MockK
    private lateinit var roleRepository: RoleRepository

    @InjectMockKs
    private lateinit var accountService: AccountService

    @Test
    fun exceptionIsThrownWHenCreatingUserAccountWithAccountIdNotZero() {
        val accountWithIdNotZero = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { AccountId(1) },
            Username::class.java to OverridePopulate { Username("username") },
            OverrideTarget.of("email", String::class.java) to OverridePopulate { "test@example.com" }
        ))
        every { roleRepository.getRoleByName(RoleName.USER) } returns USER_ROLE

        assertThatThrownBy {
            accountService.createUserAccount(
                accountWithIdNotZero,
                PASSWORD,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("invalid accountId")
    }

    @Test
    fun exceptionIsThrownWhenCreatingUserAccountWithReservedUsername() {
        val accountWithReservedUsername = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { AccountId.NEW_ACCOUNT_ID },
            Username::class.java to OverridePopulate { Username.UNKNOWN },
            OverrideTarget.of("email", String::class.java) to OverridePopulate { "test@example.com" }
        ))
        every { roleRepository.getRoleByName(RoleName.USER) } returns USER_ROLE

        assertThatThrownBy {
            accountService.createUserAccount(
                accountWithReservedUsername,
                PASSWORD,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("reserved username")
    }

    @Test
    fun exceptionIsThrownWhenCreatingUserAccountAndAccountAlreadyExists() {
        every { accountRepository.accountExists(NEW_ACCOUNT.username, NEW_ACCOUNT.email) } returns true
        every { roleRepository.getRoleByName(RoleName.USER) } returns USER_ROLE

        assertThatThrownBy {
            accountService.createUserAccount(
                NEW_ACCOUNT,
                PASSWORD,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("username or email already exists")
    }

    @Test
    fun accountIsCreatedWhenCallingCreateAccount() {
        val expectedAccountId = AccountId(100)
        every { roleRepository.getRoleByName(RoleName.USER) } returns USER_ROLE
        every { accountRepository.accountExists(NEW_ACCOUNT.username, NEW_ACCOUNT.email) } returns false
        every { accountRepository.createAccount(NEW_ACCOUNT) } returns expectedAccountId
        every { accountRoleRepository.addRoleToAccount(any(), USER_ROLE.roleId, any<Instant>()) } returns true
        every { accountPasswordRepository.createPassword(capture(accountPasswordSlot)) } returns AccountPasswordId(1)

        val accountId: AccountId = accountService.createUserAccount(NEW_ACCOUNT, PASSWORD)

        assertThat(accountId).isEqualTo(expectedAccountId)
        verify(exactly = 1) {
            accountRoleRepository.addRoleToAccount(
                expectedAccountId,
                USER_ROLE.roleId,
                any<Instant>(),
            )
        }
        val accountPassword: AccountPassword = accountPasswordSlot.captured
        assertThat(accountPassword.accountPasswordId).isEqualTo(NEW_ACCOUNT_PASSWORD_ID)
        assertThat(accountPassword.accountId).isEqualTo(accountId)
        assertThat(accountPassword.password).isEqualTo(PASSWORD)
        assertThat(accountPassword.created).isCloseTo(Instant.now(), Assertions.within(1, ChronoUnit.SECONDS))
    }

    companion object {
        val NEW_ACCOUNT: Account = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { AccountId.NEW_ACCOUNT_ID },
            Username::class.java to OverridePopulate { Username("username") },
            OverrideTarget.of("email", String::class.java) to OverridePopulate { "test@example.com" }
        ))
        private val PASSWORD: Password = Password("{bcrypt}encryptedPassword")

        val USER_ROLE = populate<Role>(mapOf(
            RoleId::class.java to OverridePopulate { RoleId(1) }
        ))
    }
}
