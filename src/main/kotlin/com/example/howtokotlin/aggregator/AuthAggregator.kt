package com.example.howtokotlin.aggregator

import com.example.howtokotlin.controller.mapper.CreateAccountDTOMapper.mapToAccount
import com.example.howtokotlin.controller.model.CreateAccountDTO
import com.example.howtokotlin.model.id.JwtToken
import com.example.howtokotlin.model.id.Password
import com.example.howtokotlin.service.AccountService
import com.example.howtokotlin.service.AuthService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthAggregator(
    private val authenticationManager: AuthenticationManager,
    private val authService: AuthService,
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
) {
    fun authenticateAndGetJwtToken(usernamePasswordAuthenticationToken: UsernamePasswordAuthenticationToken): JwtToken {
        val authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken)
        return authService.generateToken(authentication.principal as UserDetails)
    }

    fun registerAndGetJwtToken(createAccountDTO: CreateAccountDTO): JwtToken {
        accountService.createUserAccount(
            mapToAccount(createAccountDTO),
            Password(passwordEncoder.encode(createAccountDTO.password)!!),
        )
        return authenticateAndGetJwtToken(
            UsernamePasswordAuthenticationToken(
                createAccountDTO.username,
                createAccountDTO.password,
            ),
        )
    }
}
