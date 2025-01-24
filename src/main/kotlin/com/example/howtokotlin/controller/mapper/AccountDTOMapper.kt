package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.AccountDTO
import com.example.howtokotlin.model.Account

object AccountDTOMapper {
    fun mapToAccountDTOs(accounts: List<Account>): List<AccountDTO> = accounts.map { mapToAccountDTO(it) }

    fun mapToAccountDTO(account: Account): AccountDTO =
        AccountDTO(
            id = account.accountId.value,
            username = account.username.value,
            email = account.email,
            firstName = account.firstName,
            lastName = account.lastName,
        )
}
