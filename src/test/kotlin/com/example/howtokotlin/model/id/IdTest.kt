package com.example.howtokotlin.model.id

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class IdTest {
    @Test
    fun isNewInteger() {
        assertThat(AccountId(0).isNew()).isTrue()
        assertThat(AccountId(1).isNew()).isFalse()
    }

    @Test
    fun isNewBooleanThrowsException() {
        Assertions
            .assertThatThrownBy { BooleanId(true).isNew() }
            .isInstanceOf(java.lang.IllegalStateException::class.java)
            .hasMessageContaining("missing implementation for Boolean")
    }

    private class BooleanId(
        override val value: Boolean,
    ) : Id<Boolean>
}
