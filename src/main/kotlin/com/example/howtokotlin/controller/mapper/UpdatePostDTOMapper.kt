package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.UpdatePostDTO
import com.example.howtokotlin.model.Post
import java.time.Instant

object UpdatePostDTOMapper {
    fun mapToPost(
        updatePostDTO: UpdatePostDTO,
        post: Post,
    ): Post =
        post.copy(
            title = updatePostDTO.title,
            body = updatePostDTO.body,
            lastUpdated = Instant.now(),
        )
}
