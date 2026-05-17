package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountsDTOMapper.mapToAccountsDTO
import com.example.howtokotlin.controller.model.AccountsDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AccountsDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val account = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { AccountId(1) },
            Username::class.java to OverridePopulate { Username("username") },
            OverrideTarget.of("email", String::class.java) to OverridePopulate { "test@example.com" }
        ))

        val accountsDTO: AccountsDTO = mapToAccountsDTO(listOf(account))

        assertThat(accountsDTO).hasNoNullFieldsOrProperties()
        assertThat(accountsDTO.accountCount).isEqualTo(1)
        assertThat(accountsDTO.accounts).hasSize(1)
        assertThat(accountsDTO.accounts[0].email).isEqualTo("test@example.com")
    }
}
