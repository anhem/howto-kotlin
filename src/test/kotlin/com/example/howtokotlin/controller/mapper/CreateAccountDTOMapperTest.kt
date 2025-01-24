package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.CreateAccountDTOMapper.mapToAccount
import com.example.howtokotlin.controller.model.CreateAccountDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId.Companion.NEW_ACCOUNT_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CreateAccountDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val createAccountDTO =
            CreateAccountDTO(
                username = "username",
                password = "password",
                email = "email",
                firstName = "first",
                lastName = "last",
            )

        val account: Account = mapToAccount(createAccountDTO)

        assertThat(account).hasNoNullFieldsOrPropertiesExcept("lastLogin")
        assertThat(account.lastLogin).isNull()
        assertThat(account.accountId).isEqualTo(NEW_ACCOUNT_ID)
    }
}
