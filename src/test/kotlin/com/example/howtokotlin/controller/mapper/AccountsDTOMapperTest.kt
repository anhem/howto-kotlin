package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountsDTOMapper.mapToAccountsDTO
import com.example.howtokotlin.controller.model.AccountsDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class AccountsDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val account =
            Account(
                accountId = AccountId(1),
                username = Username("username"),
                firstName = "firstName",
                lastName = "lastName",
                email = "email",
                created = Instant.now(),
                lastUpdated = Instant.now(),
                lastLogin = Instant.now(),
            )

        val accountsDTO: AccountsDTO = mapToAccountsDTO(listOf(account))

        assertThat(accountsDTO).hasNoNullFieldsOrProperties()
        assertThat(accountsDTO.accountCount).isEqualTo(1)
        assertThat(accountsDTO.accounts).hasSize(1)
    }
}
