package com.example.howtokotlin.controller

import com.example.howtokotlin.controller.model.AuthenticateDTO
import com.example.howtokotlin.controller.model.CategoryDTO
import com.example.howtokotlin.controller.model.CreateAccountDTO
import com.example.howtokotlin.controller.model.MessageDTO
import com.example.howtokotlin.model.id.JwtToken
import com.example.howtokotlin.testutil.TestApplication
import com.example.howtokotlin.testutil.TestPopulator.populate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class AuthControllerIT : TestApplication() {

    @Test
    fun userCanAuthenticate() {
        val authenticateDTO =
            AuthenticateDTO(
                username = "admin",
                password = "superSecret1!",
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
        assertThat(result.responseBody!!.message).isNotBlank()
    }

    @Test
    fun userCanRegisterAndImmediatelyAccessProtectedEndpoints() {
        val uniqueSuffix = System.currentTimeMillis().toString().takeLast(6)
        val createAccountDTO =
            populate<CreateAccountDTO>("email", String::class.java) { "test$uniqueSuffix@example.com" }
                .copy(
                    username = "newuser$uniqueSuffix",
                    password = "superSecretPassword123!",
                )

        val registerResult =
            restTestClient.post()
                .uri(REGISTER_URL)
                .body(createAccountDTO)
                .exchange()
                .expectBody(MessageDTO::class.java)
                .returnResult()

        assertThat(registerResult.status).isEqualTo(HttpStatus.OK)
        assertThat(registerResult.responseBody).isNotNull()
        val tokenValue = registerResult.responseBody!!.message
        assertThat(tokenValue).isNotBlank()

        val categoriesResponse: ResponseEntity<Array<CategoryDTO>> =
            getWithToken(
                CATEGORIES_URL,
                Array<CategoryDTO>::class.java,
                JwtToken(tokenValue),
            )
        assertThat(categoriesResponse.statusCode).isEqualTo(HttpStatus.OK)
    }

    companion object {
        private const val AUTHENTICATE_URL = "/api/auth/authenticate"
        private const val REGISTER_URL = "/api/auth/register"
        private const val CATEGORIES_URL = "/api/forum/categories"
    }
}
