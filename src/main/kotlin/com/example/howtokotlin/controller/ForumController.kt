package com.example.howtokotlin.controller

import com.example.howtokotlin.aggregator.ForumAggregator
import com.example.howtokotlin.configuration.JwtTokenFilter.Companion.BEARER_AUTHENTICATION
import com.example.howtokotlin.controller.mapper.CategoryDTOMapper.mapToCategoryDTO
import com.example.howtokotlin.controller.mapper.CategoryDTOMapper.mapToCategoryDTOs
import com.example.howtokotlin.controller.mapper.UpsertCategoryDTOMapper.mapToCategory
import com.example.howtokotlin.controller.model.*
import com.example.howtokotlin.controller.model.MessageDTO.Companion.fromId
import com.example.howtokotlin.model.RoleName.Constants.ADMINISTRATOR
import com.example.howtokotlin.model.RoleName.Constants.MODERATOR
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import com.example.howtokotlin.service.ForumService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["api/forum"])
@SecurityRequirement(name = BEARER_AUTHENTICATION)
class ForumController(
    private val forumAggregator: ForumAggregator,
    private val forumService: ForumService,
) {
    @GetMapping("categories")
    fun getCategories(): List<CategoryDTO> = mapToCategoryDTOs(forumService.getCategories())

    @GetMapping("categories/{categoryId}")
    fun getCategory(
        @PathVariable categoryId: Int,
    ): CategoryDTO = mapToCategoryDTO(forumService.getCategory(CategoryId(categoryId)))

    @Secured(MODERATOR, ADMINISTRATOR)
    @PostMapping(value = ["categories"])
    fun createCategory(
        @RequestBody upsertCategoryDTO: @Valid UpsertCategoryDTO,
    ): MessageDTO = fromId(forumService.createCategory(mapToCategory(upsertCategoryDTO)))

    @Secured(MODERATOR, ADMINISTRATOR)
    @PutMapping("categories/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: Int,
        @RequestBody upsertCategoryDTO: @Valid UpsertCategoryDTO,
    ): CategoryDTO = forumAggregator.updateCategory(CategoryId(categoryId), upsertCategoryDTO)

    @Secured(ADMINISTRATOR)
    @DeleteMapping(value = ["categories/{categoryId}"])
    fun removeCategory(
        @PathVariable categoryId: Int,
    ): MessageDTO {
        forumService.removeCategory(CategoryId(categoryId))
        return MessageDTO.OK
    }

    @GetMapping("categories/{categoryId}/posts")
    fun getPosts(
        @PathVariable categoryId: Int,
    ): List<PostDTO> = forumAggregator.getPosts(CategoryId(categoryId))

    @GetMapping("posts/{postId}")
    fun getPost(
        @PathVariable postId: Int,
    ): PostDTO = forumAggregator.getPost(PostId(postId))

    @PostMapping("posts")
    fun createPost(
        @RequestBody createPostDTO: @Valid CreatePostDTO,
    ): MessageDTO = forumAggregator.createPost(createPostDTO)

    @PutMapping("posts/{postId}")
    fun updatePost(
        @PathVariable postId: Int,
        @RequestBody updatePostDTO: @Valid UpdatePostDTO,
    ): PostDTO = forumAggregator.updatePost(PostId(postId), updatePostDTO)

    @DeleteMapping("posts/{postId}")
    fun removePost(
        @PathVariable postId: Int,
    ): MessageDTO {
        forumAggregator.removePost(PostId(postId))
        return MessageDTO.OK
    }

    @GetMapping("posts/{postId}/replies")
    fun getReplies(
        @PathVariable postId: Int,
    ): List<ReplyDTO> = forumAggregator.getReplies(PostId(postId))

    @GetMapping("replies/{replyId}")
    fun getReply(
        @PathVariable replyId: Int,
    ): ReplyDTO = forumAggregator.getReply(ReplyId(replyId))

    @PostMapping("replies")
    fun createReply(
        @RequestBody createReplyDTO: @Valid CreateReplyDTO,
    ): MessageDTO = forumAggregator.createReply(createReplyDTO)

    @PutMapping("replies/{replyId}")
    fun updateReply(
        @PathVariable replyId: Int,
        @RequestBody updateReplyDTO: @Valid UpdateReplyDTO,
    ): ReplyDTO = forumAggregator.updateReply(ReplyId(replyId), updateReplyDTO)

    @DeleteMapping("replies/{replyId}")
    fun removeReply(
        @PathVariable replyId: Int,
    ): MessageDTO {
        forumAggregator.removeReply(ReplyId(replyId))
        return MessageDTO.OK
    }
}
