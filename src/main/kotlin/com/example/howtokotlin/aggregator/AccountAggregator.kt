package com.example.howtokotlin.aggregator

import com.example.howtokotlin.controller.mapper.AccountDetailsDTOMapper.mapToAccountDetailsDTO
import com.example.howtokotlin.controller.model.AccountDetailsDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.repository.AccountRoleRepository
import com.example.howtokotlin.service.AccountService
import org.springframework.stereotype.Component

@Component
class AccountAggregator(
    private val accountService: AccountService,
    private val accountRoleRepository: AccountRoleRepository,
) {
    fun getAccountDetails(accountId: AccountId): AccountDetailsDTO {
        val account: Account = accountService.getAccount(accountId)
        val roleNames: List<RoleName> = accountRoleRepository.getRoleNames(accountId)
        return mapToAccountDetailsDTO(account, roleNames)
    }
}
