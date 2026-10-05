package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.UpdatePostDTOMapper.mapToPost
import com.example.howtokotlin.controller.model.UpdatePostDTO
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UpdatePostDTOMapperTest {
    @Test
    fun mappedToModel() {
        val updatePostDTO = populate<UpdatePostDTO>(mapOf(
            OverrideTarget.of("title", String::class.java) to OverridePopulate { "title2" },
            OverrideTarget.of("body", String::class.java) to OverridePopulate { "body2" }
        ))
        val post = populate<Post>(mapOf(
            PostId::class.java to OverridePopulate { PostId(1) },
            CategoryId::class.java to OverridePopulate { CategoryId(2) },
            AccountId::class.java to OverridePopulate { AccountId(3) },
            OverrideTarget.of("lastUpdated", java.time.Instant::class.java) to OverridePopulate { java.time.Instant.now().minusSeconds(10) }
        ))
        val updatedPost: Post = mapToPost(updatePostDTO, post)

        assertThat(updatedPost).hasNoNullFieldsOrProperties()
        assertThat(updatedPost.title).isEqualTo(updatePostDTO.title)
        assertThat(updatedPost.body).isEqualTo(updatePostDTO.body)
        assertThat(updatedPost.lastUpdated).isAfter(post.lastUpdated)
        assertThat(updatedPost).usingRecursiveComparison()
            .ignoringFields("title", "body", "lastUpdated")
            .isEqualTo(post)
    }
}
