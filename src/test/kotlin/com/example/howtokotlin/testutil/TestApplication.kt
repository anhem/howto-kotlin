package com.example.howtokotlin.testutil

import com.example.howtokotlin.configuration.JwtTokenFilter.Companion.BEARER
import com.example.howtokotlin.controller.model.AuthenticateDTO
import com.example.howtokotlin.controller.model.MessageDTO
import com.example.howtokotlin.model.id.JwtToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.client.RestTestClient
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile.forClasspathResource

@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
abstract class TestApplication {
    companion object {
        private const val ADMIN_USERNAME = "admin"
        private const val ADMIN_PASSWORD = "superSecret1!"
        private const val MODERATOR_USERNAME = "moderator"
        private const val MODERATOR_PASSWORD = "superSecret2!"
        private const val USER_USERNAME = "user"
        private const val USER_PASSWORD = "superSecret3!"

        private const val AUTHENTICATE_URL = "/api/auth/authenticate"

        private val SQL_CONTAINER: PostgreSQLContainer =
            PostgreSQLContainer("postgres:14.5")
                .withDatabaseName("howto-db-it")
                .withUsername("howto")
                .withCopyFileToContainer(
                    forClasspathResource("/db/baseline/howto_db_baseline.sql"),
                    "/docker-entrypoint-initdb.d/howto_db_baseline.sql",
                )

        @JvmStatic
        @DynamicPropertySource
        fun registerSQLProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { SQL_CONTAINER.jdbcUrl }
            registry.add("spring.datasource.hikari.username") { SQL_CONTAINER.username }
            registry.add("spring.datasource.hikari.password") { SQL_CONTAINER.password }
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            SQL_CONTAINER.start()
        }

        private fun withJwtToken(jwtToken: JwtToken): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.add(HttpHeaders.AUTHORIZATION, java.lang.String.format("%s%s", BEARER, jwtToken.value))
            return httpHeaders
        }
    }

    protected lateinit var adminJwtToken: JwtToken
    protected lateinit var moderatorJwtToken: JwtToken
    protected lateinit var userJwtToken: JwtToken

    @BeforeEach
    fun setUpTestApplication() {
        if (!::adminJwtToken.isInitialized) {
            adminJwtToken = authenticate(ADMIN_USERNAME, ADMIN_PASSWORD)
        }
        if (!::moderatorJwtToken.isInitialized) {
            moderatorJwtToken = authenticate(MODERATOR_USERNAME, MODERATOR_PASSWORD)
        }
        if (!::userJwtToken.isInitialized) {
            userJwtToken = authenticate(USER_USERNAME, USER_PASSWORD)
        }
    }

    @Autowired
    protected lateinit var restTestClient: RestTestClient

    protected fun <T : Any> getWithToken(
        url: String,
        responseType: Class<T>,
        jwtToken: JwtToken,
    ): ResponseEntity<T> {
        val result =
            restTestClient.get()
                .uri(url)
                .headers { headers -> headers.addAll(withJwtToken(jwtToken)) }
                .exchange()
                .expectBody(responseType)
                .returnResult()
        return ResponseEntity(result.responseBody, result.responseHeaders, result.status)
    }

    protected fun <T : Any, B : Any> postWithToken(
        url: String,
        body: B,
        responseType: Class<T>,
        jwtToken: JwtToken,
    ): ResponseEntity<T> {
        val result =
            restTestClient.post()
                .uri(url)
                .headers { headers -> headers.addAll(withJwtToken(jwtToken)) }
                .body(body)
                .exchange()
                .expectBody(responseType)
                .returnResult()
        return ResponseEntity(result.responseBody, result.responseHeaders, result.status)
    }

    protected fun <T : Any, B : Any> putWithToken(
        url: String,
        body: B,
        responseType: Class<T>,
        jwtToken: JwtToken,
    ): ResponseEntity<T> {
        val result =
            restTestClient.put()
                .uri(url)
                .headers { headers -> headers.addAll(withJwtToken(jwtToken)) }
                .body(body)
                .exchange()
                .expectBody(responseType)
                .returnResult()
        return ResponseEntity(result.responseBody, result.responseHeaders, result.status)
    }

    protected fun <T : Any> deleteWithToken(
        url: String,
        responseType: Class<T>,
        jwtToken: JwtToken,
    ): ResponseEntity<T> {
        val result =
            restTestClient.delete()
                .uri(url)
                .headers { headers -> headers.addAll(withJwtToken(jwtToken)) }
                .exchange()
                .expectBody(responseType)
                .returnResult()
        return ResponseEntity(result.responseBody, result.responseHeaders, result.status)
    }

    private fun authenticate(
        username: String,
        password: String,
    ): JwtToken {
        val authenticateDTO =
            AuthenticateDTO(
                username = username,
                password = password,
            )
        val result =
            restTestClient.post()
                .uri(AUTHENTICATE_URL)
                .body(authenticateDTO)
                .exchange()
                .expectBody(MessageDTO::class.java)
                .returnResult()
        assertThat(result.status).isEqualTo(HttpStatus.OK)
        assertThat(result.responseBody).isNotNull()
        return JwtToken(result.responseBody!!.message)
    }
}
