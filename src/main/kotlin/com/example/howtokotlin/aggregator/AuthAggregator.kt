package com.example.howtokotlin.aggregator

import com.example.howtokotlin.model.id.JwtToken
import com.example.howtokotlin.service.AuthService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class AuthAggregator(
    private val authenticationManager: AuthenticationManager,
    private var authService: AuthService,
) {
    fun authenticateAndGetJwtToken(usernamePasswordAuthenticationToken: UsernamePasswordAuthenticationToken?): JwtToken {
        val authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken)
        return authService.generateToken(authentication.principal as UserDetails)
    }
}
