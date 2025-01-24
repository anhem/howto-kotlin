package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.UpdatePostDTOMapper.mapToPost
import com.example.howtokotlin.controller.model.UpdatePostDTO
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class UpdatePostDTOMapperTest {
    @Test
    fun mappedToModel() {
        val updatePostDTO =
            UpdatePostDTO(
                title = "title2",
                body = "body2",
            )
        val post =
            Post(
                postId = PostId(1),
                categoryId = CategoryId(2),
                accountId = AccountId(3),
                title = "title",
                body = "body",
                created = Instant.now(),
                lastUpdated = Instant.now(),
            )
        val updatedPost: Post = mapToPost(updatePostDTO, post)

        assertThat(updatedPost).hasNoNullFieldsOrProperties()
        assertThat(updatedPost).isNotEqualTo(post)
    }
}
