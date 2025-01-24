package com.example.howtokotlin.controller

import com.example.howtokotlin.configuration.JwtTokenFilter.Companion.BEARER_AUTHENTICATION
import com.example.howtokotlin.controller.model.MessageDTO
import com.example.howtokotlin.model.RoleName.Constants.ADMINISTRATOR
import com.example.howtokotlin.model.RoleName.Constants.MODERATOR
import com.example.howtokotlin.model.RoleName.Constants.USER
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["api/hello-world"])
@SecurityRequirement(name = BEARER_AUTHENTICATION)
class HelloWorldController {
    companion object {
        const val HELLO_WORLD: String = "Hello world"
        const val HELLO_AUTHENTICATED_USER: String = "Hello authenticated user"
        const val HELLO_ADMINISTRATOR: String = "Hello administrator"
    }

    @GetMapping
    fun hello(): MessageDTO = MessageDTO(HELLO_WORLD)

    @Secured(USER, MODERATOR, ADMINISTRATOR)
    @GetMapping("authenticated")
    fun helloAuthenticated(): MessageDTO = MessageDTO(HELLO_AUTHENTICATED_USER)

    @Secured(ADMINISTRATOR)
    @GetMapping(value = ["authenticated/administrator"])
    fun helloAdministrator(): MessageDTO = MessageDTO(HELLO_ADMINISTRATOR)
}
