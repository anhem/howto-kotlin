package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountDetailsDTOMapper.mapToAccountDetailsDTO
import com.example.howtokotlin.controller.model.AccountDetailsDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.RoleName.ADMINISTRATOR
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class AccountDetailsDTOMapperTest {
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

        val roleNames = listOf(ADMINISTRATOR)
        val accountDetailsDTO: AccountDetailsDTO = mapToAccountDetailsDTO(account, roleNames)

        assertThat(accountDetailsDTO).hasNoNullFieldsOrProperties()
        assertThat(accountDetailsDTO.account.id).isEqualTo(account.accountId.value)
        assertThat(accountDetailsDTO.created).isEqualTo(account.created)
        assertThat(accountDetailsDTO.update).isEqualTo(account.lastUpdated)
        assertThat(accountDetailsDTO.roles).hasSize(roleNames.size)
        assertThat(accountDetailsDTO.roles).contains(roleNames[0].role)
    }
}
