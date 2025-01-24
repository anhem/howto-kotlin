package com.example.howtokotlin.controller

import com.example.howtokotlin.controller.HelloWorldController.Companion.HELLO_ADMINISTRATOR
import com.example.howtokotlin.controller.HelloWorldController.Companion.HELLO_AUTHENTICATED_USER
import com.example.howtokotlin.controller.HelloWorldController.Companion.HELLO_WORLD
import com.example.howtokotlin.controller.model.ErrorCode.ACCESS_DENIED
import com.example.howtokotlin.controller.model.ErrorDTO
import com.example.howtokotlin.controller.model.MessageDTO
import com.example.howtokotlin.testutil.TestApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class HelloWorldControllerIT : TestApplication() {
    companion object {
        const val GET_URL: String = "/api/hello-world"
        const val GET_AUTHENTICATED_URL: String = "/api/hello-world/authenticated"
        const val GET_ADMINISTRATOR_URL: String = "/api/hello-world/authenticated/administrator"
    }

    @Test
    fun unauthenticatedCanGetHelloWorld() {
        val response: ResponseEntity<MessageDTO> = testRestTemplate.getForEntity(GET_URL, MessageDTO::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.message).isEqualTo(HELLO_WORLD)
    }

    @Test
    fun userCanGetHelloAuthenticated() {
        val response: ResponseEntity<MessageDTO> =
            getWithToken(
                GET_AUTHENTICATED_URL,
                MessageDTO::class.java,
                userJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.message).isEqualTo(HELLO_AUTHENTICATED_USER)
    }

    @Test
    fun adminCanGetHelloAuthenticated() {
        val response: ResponseEntity<MessageDTO> =
            getWithToken(
                GET_AUTHENTICATED_URL,
                MessageDTO::class.java,
                adminJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.message).isEqualTo(HELLO_AUTHENTICATED_USER)
    }

    @Test
    fun adminCanGetHelloAdministrator() {
        val response: ResponseEntity<MessageDTO> =
            getWithToken(
                GET_ADMINISTRATOR_URL,
                MessageDTO::class.java,
                adminJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.message).isEqualTo(HELLO_ADMINISTRATOR)
    }

    @Test
    fun userCannotGetHelloAdministrator() {
        val response: ResponseEntity<ErrorDTO> =
            getWithToken(
                GET_ADMINISTRATOR_URL,
                ErrorDTO::class.java,
                userJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.errorCode).isEqualTo(ACCESS_DENIED)
    }
}
