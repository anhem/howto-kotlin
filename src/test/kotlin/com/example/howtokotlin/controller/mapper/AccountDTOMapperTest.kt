package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountDTOMapper.mapToAccountDTOs
import com.example.howtokotlin.controller.model.AccountDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AccountDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val account = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { AccountId(1) },
            Username::class.java to OverridePopulate { Username("username") },
            OverrideTarget.of("email", String::class.java) to OverridePopulate { "test@example.com" }
        ))

        val accountDTOs: List<AccountDTO> = mapToAccountDTOs(listOf(account))

        assertThat(accountDTOs).hasSize(1)
        val accountDTO: AccountDTO = accountDTOs[0]
        assertThat(accountDTO).hasNoNullFieldsOrProperties()
        assertThat(accountDTO.id).isEqualTo(account.accountId.value)
        assertThat(accountDTO.email).isEqualTo("test@example.com")
    }
}
