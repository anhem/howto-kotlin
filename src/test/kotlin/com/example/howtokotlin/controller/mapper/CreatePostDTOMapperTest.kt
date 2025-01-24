package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.CreatePostDTOMapper.mapToPost
import com.example.howtokotlin.controller.model.CreatePostDTO
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.AccountId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CreatePostDTOMapperTest {
    @Test
    fun mappedToModel() {
        val createPostDTO =
            CreatePostDTO(
                categoryId = 1,
                title = "title",
                body = "body",
            )
        val accountId = AccountId(1)

        val post: Post = mapToPost(createPostDTO, accountId)

        assertThat(post).hasNoNullFieldsOrProperties()
    }
}
