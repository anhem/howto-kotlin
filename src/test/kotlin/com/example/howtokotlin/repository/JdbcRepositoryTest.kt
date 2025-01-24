package com.example.howtokotlin.repository

import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.AccountPasswordId
import com.example.howtokotlin.model.id.PostId
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@ExtendWith(MockKExtension::class)
internal class JdbcRepositoryTest {
    @MockK
    private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

    @InjectMockKs
    private lateinit var arbitraryRepository: ArbitraryRepository

    @Test
    fun primaryKeyNameReturnsClassNameWithUnderscore() {
        assertThat(arbitraryRepository.getPrimaryKeyName(PostId::class.java)).isEqualTo("post_id")
        assertThat(arbitraryRepository.getPrimaryKeyName(AccountId::class.java)).isEqualTo("account_id")
        assertThat(arbitraryRepository.getPrimaryKeyName(AccountPasswordId::class.java)).isEqualTo("account_password_id")
    }

    class ArbitraryRepository(
        namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    ) : JdbcRepository(namedParameterJdbcTemplate)
}
