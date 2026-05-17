package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.CreateAccountDTOMapper.mapToAccount
import com.example.howtokotlin.controller.model.CreateAccountDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId.Companion.NEW_ACCOUNT_ID
import com.example.howtokotlin.testutil.TestPopulator.populate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CreateAccountDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val createAccountDTO = populate<CreateAccountDTO>("email", String::class.java) { "test@example.com" }

        val account: Account = mapToAccount(createAccountDTO)

        assertThat(account).hasNoNullFieldsOrPropertiesExcept("lastLogin")
        assertThat(account.lastLogin).isNull()
        assertThat(account.accountId).isEqualTo(NEW_ACCOUNT_ID)
        assertThat(account.email).isEqualTo("test@example.com")
    }
}
