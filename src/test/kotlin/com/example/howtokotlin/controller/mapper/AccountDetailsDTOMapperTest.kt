package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountDetailsDTOMapper.mapToAccountDetailsDTO
import com.example.howtokotlin.controller.model.AccountDetailsDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.RoleName.ADMINISTRATOR
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccountDetailsDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val account = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { AccountId(1) },
            Username::class.java to OverridePopulate { Username("username") },
            OverrideTarget.of("email", String::class.java) to OverridePopulate { "test@example.com" }
        ))

        val roleNames = listOf(ADMINISTRATOR)
        val accountDetailsDTO: AccountDetailsDTO = mapToAccountDetailsDTO(account, roleNames)

        assertThat(accountDetailsDTO).hasNoNullFieldsOrProperties()
        assertThat(accountDetailsDTO.account.id).isEqualTo(account.accountId.value)
        assertThat(accountDetailsDTO.account.email).isEqualTo("test@example.com")
        assertThat(accountDetailsDTO.created).isEqualTo(account.created)
        assertThat(accountDetailsDTO.update).isEqualTo(account.lastUpdated)
        assertThat(accountDetailsDTO.roles).hasSize(roleNames.size)
        assertThat(accountDetailsDTO.roles).contains(roleNames[0].role)
    }
}
