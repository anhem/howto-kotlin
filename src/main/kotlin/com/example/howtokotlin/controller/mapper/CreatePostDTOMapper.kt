package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.CreatePostDTO
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import java.time.Instant

object CreatePostDTOMapper {
    fun mapToPost(
        createPostDTO: CreatePostDTO,
        accountId: AccountId,
    ): Post {
        val now = Instant.now()
        return Post(
            postId = PostId.NEW_POST_ID,
            categoryId = CategoryId(createPostDTO.categoryId),
            accountId = accountId,
            title = createPostDTO.title,
            body = createPostDTO.body,
            created = now,
            lastUpdated = now,
        )
    }
}
