package com.example.howtokotlin.configuration

import com.example.howtokotlin.model.id.JwtToken
import com.example.howtokotlin.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*

@Component
class JwtTokenFilter(
    private val authService: AuthService,
) : OncePerRequestFilter() {
    companion object {
        const val BEARER: String = "Bearer "
        const val BEARER_AUTHENTICATION: String = BEARER + "Authentication"

        private fun getJwtToken(request: HttpServletRequest): Optional<JwtToken> {
            val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION)
            if (bearerToken != null && bearerToken.startsWith(BEARER)) {
                return Optional.of<JwtToken>(JwtToken(bearerToken.substring(BEARER.length)))
            }
            return Optional.empty<JwtToken>()
        }
    }

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val jwtToken: Optional<JwtToken> = getJwtToken(request)
        jwtToken.ifPresent { token: JwtToken ->
            try {
                if (authService.validateToken(token)) {
                    authService.setSecurityContext(WebAuthenticationDetailsSource().buildDetails(request), token)
                }
            } catch (e: Exception) {
                log.error("Could not validate token", e)
            }
        }
        filterChain.doFilter(request, response)
    }
}
