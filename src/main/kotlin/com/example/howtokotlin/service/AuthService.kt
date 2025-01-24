package com.example.howtokotlin.service

import com.example.howtokotlin.configuration.HowtoConfig
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.AccountPassword
import com.example.howtokotlin.model.RoleName
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.JwtToken
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.repository.AccountPasswordRepository
import com.example.howtokotlin.repository.AccountRepository
import com.example.howtokotlin.repository.AccountRoleRepository
import com.example.howtokotlin.util.JwtUtil
import com.example.howtokotlin.util.JwtUtil.getRoles
import com.example.howtokotlin.util.JwtUtil.getUsername
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val accountRepository: AccountRepository,
    private val accountRoleRepository: AccountRoleRepository,
    private val accountPasswordRepository: AccountPasswordRepository,
    howtoConfig: HowtoConfig,
) : UserDetailsService {
    private val jwtSecret: String = howtoConfig.jwtSecret!!

    override fun loadUserByUsername(username: String): UserDetails {
        val account: Account = accountRepository.getAccount(Username(username))
        val accountPassword: AccountPassword = accountPasswordRepository.getPassword(account.accountId)
        val roleNames: List<RoleName> = accountRoleRepository.getRoleNames(account.accountId)

        val simpleGrantedAuthorities: List<SimpleGrantedAuthority> =
            roleNames.map { roleName: RoleName -> SimpleGrantedAuthority(roleName.role) }

        return User(
            account.username.value,
            accountPassword.password.value,
            simpleGrantedAuthorities,
        )
    }

    fun generateToken(userDetails: UserDetails): JwtToken = JwtUtil.generateToken(userDetails, jwtSecret)

    fun validateToken(jwtToken: JwtToken): Boolean = JwtUtil.validateToken(jwtToken, jwtSecret)

    fun setSecurityContext(
        webAuthenticationDetails: WebAuthenticationDetails,
        jwtToken: JwtToken,
    ) {
        val username: Username = getUsername(jwtToken, jwtSecret)
        val roles: List<SimpleGrantedAuthority> = getRoles(jwtToken, jwtSecret)
        val userDetails: UserDetails = User(username.value, "", roles)
        val authentication: UsernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities,
            )
        authentication.details = webAuthenticationDetails
        SecurityContextHolder.getContext().authentication = authentication
    }

    val loggedInAccountId: AccountId
        get() {
            val userDetails: UserDetails = getUserDetailsFromSecurityContext()
            return accountRepository.getAccount(Username(userDetails.username)).accountId
        }

    fun loggedInAccountHasAnyOf(roleNames: List<RoleName>): Boolean {
        val loggedInRoleNames: List<RoleName> = getLoggedInRoleNames()
        return roleNames.stream().anyMatch { o: RoleName -> loggedInRoleNames.contains(o) }
    }

    private fun getLoggedInRoleNames(): List<RoleName> =
        getUserDetailsFromSecurityContext()
            .authorities
            .stream()
            .map { grantedAuthority -> RoleName.fromRole(grantedAuthority.authority) }
            .toList()

    private fun getUserDetailsFromSecurityContext(): UserDetails =
        SecurityContextHolder.getContext().authentication.principal as UserDetails
}
