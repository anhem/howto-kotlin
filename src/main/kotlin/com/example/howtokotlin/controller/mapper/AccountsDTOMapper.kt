package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountDTOMapper.mapToAccountDTOs
import com.example.howtokotlin.controller.model.AccountsDTO
import com.example.howtokotlin.model.Account

object AccountsDTOMapper {
    fun mapToAccountsDTO(accounts: List<Account>): AccountsDTO {
        val mapToAccountDTOs = mapToAccountDTOs(accounts)
        return AccountsDTO(
            accountCount = mapToAccountDTOs.size,
            accounts = mapToAccountDTOs,
        )
    }
}
