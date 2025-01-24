package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.CreateAccountDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import java.time.Instant

object CreateAccountDTOMapper {
    fun mapToAccount(createAccountDTO: CreateAccountDTO): Account {
        val now = Instant.now()
        return Account(
            accountId = AccountId.NEW_ACCOUNT_ID,
            username = Username(createAccountDTO.username),
            email = createAccountDTO.email,
            firstName = createAccountDTO.firstName,
            lastName = createAccountDTO.lastName,
            created = now,
            lastUpdated = now,
        )
    }
}
