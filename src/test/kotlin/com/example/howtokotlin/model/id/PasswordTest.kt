package com.example.howtokotlin.model.id

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

internal class PasswordTest {
    @Test
    fun runtimeExceptionWhenValueIsEmpty() {
        assertThrows(RuntimeException::class.java) { Password("") }
    }

    @Test
    fun runtimeExceptionWhenValueNotEncoded() {
        assertThrows(
            RuntimeException::class.java,
        ) { Password(RAW_PASSWORD) }
    }

    @Test
    fun passwordCreatedWhenValueIsEncoded() {
        val encodePassword = PASSWORD_ENCODER.encode(RAW_PASSWORD)

        val password = Password(encodePassword)

        assertThat(password.value).isNotBlank()
    }

    companion object {
        val PASSWORD_ENCODER: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        const val RAW_PASSWORD: String = "abc123"
    }
}
