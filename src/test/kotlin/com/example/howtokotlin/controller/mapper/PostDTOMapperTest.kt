package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.PostDTOMapper.mapToPostDTOs
import com.example.howtokotlin.controller.model.PostDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PostDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val accountId = AccountId(1)
        val account = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { accountId },
            Username::class.java to OverridePopulate { Username("username") },
            OverrideTarget.of("email", String::class.java) to OverridePopulate { "test@example.com" }
        ))
        val post = populate<Post>(mapOf(
            PostId::class.java to OverridePopulate { PostId(2) },
            CategoryId::class.java to OverridePopulate { CategoryId(3) },
            AccountId::class.java to OverridePopulate { accountId }
        ))

        val postDTOs: List<PostDTO> = mapToPostDTOs(listOf(post), listOf(account))

        assertThat(postDTOs).hasSize(1)
        assertThat(postDTOs[0]).hasNoNullFieldsOrProperties()
        assertThat(postDTOs[0].username).isEqualTo("username")
    }
}
