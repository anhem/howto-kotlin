package com.example.howtokotlin.controller

import com.example.howtokotlin.aggregator.AuthAggregator
import com.example.howtokotlin.controller.model.AuthenticateDTO
import com.example.howtokotlin.controller.model.MessageDTO
import com.example.howtokotlin.model.id.JwtToken
import jakarta.validation.Valid
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["api/auth"])
class AuthController(
    private val authAggregator: AuthAggregator,
) {
    @PostMapping(value = ["authenticate"])
    fun authenticate(
        @RequestBody authenticateDTO: @Valid AuthenticateDTO,
    ): MessageDTO {
        val jwtToken: JwtToken =
            authAggregator.authenticateAndGetJwtToken(
                UsernamePasswordAuthenticationToken(
                    authenticateDTO.username,
                    authenticateDTO.password,
                ),
            )
        return MessageDTO(jwtToken.value)
    }
}
