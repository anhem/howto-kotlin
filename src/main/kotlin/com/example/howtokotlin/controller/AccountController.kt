package com.example.howtokotlin.controller

import com.example.howtokotlin.aggregator.AccountAggregator
import com.example.howtokotlin.configuration.JwtTokenFilter.Companion.BEARER_AUTHENTICATION
import com.example.howtokotlin.controller.mapper.AccountDTOMapper.mapToAccountDTOs
import com.example.howtokotlin.controller.mapper.AccountsDTOMapper.mapToAccountsDTO
import com.example.howtokotlin.controller.mapper.CreateAccountDTOMapper.mapToAccount
import com.example.howtokotlin.controller.model.AccountDetailsDTO
import com.example.howtokotlin.controller.model.CreateAccountDTO
import com.example.howtokotlin.controller.model.MessageDTO
import com.example.howtokotlin.controller.model.MessageDTO.Companion.fromId
import com.example.howtokotlin.model.RoleName.Constants.ADMINISTRATOR
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Password
import com.example.howtokotlin.service.AccountService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@Secured(ADMINISTRATOR)
@RequestMapping(value = ["api/accounts"])
@SecurityRequirement(name = BEARER_AUTHENTICATION)
class AccountController(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
    private val accountAggregator: AccountAggregator,
) {
    @GetMapping
    fun getAccounts() = mapToAccountsDTO(accountService.getUsers())

    @GetMapping(value = ["details/{accountId}"])
    fun getAccountDetails(
        @PathVariable accountId: Int,
    ): AccountDetailsDTO = accountAggregator.getAccountDetails(AccountId(accountId))

    @PostMapping(value = ["users/user"])
    fun createUserAccount(
        @RequestBody createAccountDTO: @Valid CreateAccountDTO,
    ): MessageDTO =
        fromId(
            accountService.createUserAccount(
                mapToAccount(createAccountDTO),
                Password(passwordEncoder.encode(createAccountDTO.password)),
            ),
        )

    @PostMapping(value = ["users/administrator"])
    fun createAdministratorAccount(
        @RequestBody createAccountDTO: @Valid CreateAccountDTO,
    ): MessageDTO =
        fromId(
            accountService.createAdministratorAccount(
                mapToAccount(createAccountDTO),
                Password(passwordEncoder.encode(createAccountDTO.password)),
            ),
        )

    @DeleteMapping(value = ["{accountId}"])
    fun removeAccount(
        @PathVariable accountId: Int,
    ): MessageDTO {
        accountService.removeAccount(AccountId(accountId))
        return MessageDTO.OK
    }
}
