package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.PostDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.model.id.Username.Companion.UNKNOWN

object PostDTOMapper {
    fun mapToPostDTOs(
        posts: List<Post>,
        accounts: List<Account>,
    ): List<PostDTO> = posts.map { mapToPostDTO(it, accounts) }

    fun mapToPostDTO(
        post: Post,
        accounts: List<Account>,
    ): PostDTO {
        val username: Username =
            accounts
                .firstOrNull { it.accountId == post.accountId }
                ?.username ?: UNKNOWN

        return PostDTO(
            postId = post.postId.value,
            categoryId = post.categoryId.value,
            username = username.value,
            title = post.title,
            body = post.body,
            created = post.created,
            updated = post.lastUpdated,
        )
    }
}
