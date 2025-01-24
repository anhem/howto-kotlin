package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountDTOMapper.mapToAccountDTOs
import com.example.howtokotlin.controller.model.AccountDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class AccountDTOMapperTest {
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

        val accountDTOs: List<AccountDTO> = mapToAccountDTOs(listOf(account))

        assertThat(accountDTOs).hasSize(1)
        val accountDTO: AccountDTO = accountDTOs[0]
        assertThat(accountDTO).hasNoNullFieldsOrProperties()
        assertThat(accountDTO.id).isEqualTo(account.accountId.value)
    }
}
