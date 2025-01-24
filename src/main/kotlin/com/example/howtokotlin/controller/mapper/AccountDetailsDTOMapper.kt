package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.AccountDTOMapper.mapToAccountDTO
import com.example.howtokotlin.controller.model.AccountDetailsDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.RoleName

object AccountDetailsDTOMapper {
    fun mapToAccountDetailsDTO(
        account: Account,
        roleNames: List<RoleName>,
    ): AccountDetailsDTO =
        AccountDetailsDTO(
            account = mapToAccountDTO(account),
            created = account.created,
            update = account.lastUpdated,
            roles = roleNames.map { it.role },
        )
}
