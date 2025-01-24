package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.PostDTOMapper.mapToPostDTOs
import com.example.howtokotlin.controller.model.PostDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.Username
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class PostDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val account =
            Account(
                accountId = AccountId(1),
                username = Username("username"),
                firstName = "firstName",
                lastName = "lastName",
                email = "email",
                created = Instant.now(),
                lastUpdated = Instant.now(),
                lastLogin = Instant.now(),
            )
        val post =
            Post(
                postId = PostId(2),
                categoryId = CategoryId(3),
                accountId = account.accountId,
                title = "title",
                body = "body",
                created = Instant.now(),
                lastUpdated = Instant.now(),
            )

        val postDTOs: List<PostDTO> = mapToPostDTOs(listOf(post), listOf(account))

        assertThat(postDTOs).hasSize(1)
        assertThat(postDTOs[0]).hasNoNullFieldsOrProperties()
    }
}
