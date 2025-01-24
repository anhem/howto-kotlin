package com.example.howtokotlin.util

import com.example.howtokotlin.model.id.JwtToken
import com.example.howtokotlin.model.id.Username
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

object JwtUtil {
    private const val ONE_HOUR_IN_MS = 60 * 60 * 1000
    private const val ROLES = "ROLES"
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun generateToken(
        userDetails: UserDetails,
        jwtSecret: String,
    ): JwtToken {
        val roles = userDetails.authorities.map { obj: GrantedAuthority -> obj.authority }

        return JwtToken(
            Jwts
                .builder()
                .claims(mapOf(ROLES to roles))
                .subject(userDetails.username)
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + ONE_HOUR_IN_MS))
                .signWith(toKey(jwtSecret))
                .compact(),
        )
    }

    fun validateToken(
        jwtToken: JwtToken,
        jwtSecret: String,
    ): Boolean {
        try {
            getClaims(jwtToken, jwtSecret)
            return true
        } catch (e: Exception) {
            log.warn("Token invalid", e)
            return false
        }
    }

    fun getUsername(
        jwtToken: JwtToken,
        jwtSecret: String,
    ): Username = Username(getClaims(jwtToken, jwtSecret).getSubject())

    fun getRoles(
        jwtToken: JwtToken,
        jwtSecret: String,
    ): List<SimpleGrantedAuthority> {
        val roles = getClaims(jwtToken, jwtSecret).getOrDefault(ROLES, emptyList<Any>())

        return if (roles is List<*>) {
            roles.filterIsInstance<String>().map { role -> SimpleGrantedAuthority(role) }
        } else {
            emptyList()
        }
    }

    private fun getClaims(
        jwtToken: JwtToken,
        jwtSecret: String,
    ): Claims =
        Jwts
            .parser()
            .verifyWith(toKey(jwtSecret))
            .build()
            .parseSignedClaims(jwtToken.value)
            .payload

    private fun toKey(jwtSecret: String): SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))
}
