package com.example.howtokotlin.aggregator

import com.example.howtokotlin.controller.mapper.CategoryDTOMapper.mapToCategoryDTO
import com.example.howtokotlin.controller.mapper.CreatePostDTOMapper.mapToPost
import com.example.howtokotlin.controller.mapper.CreateReplyDTOMapper
import com.example.howtokotlin.controller.mapper.PostDTOMapper.mapToPostDTO
import com.example.howtokotlin.controller.mapper.PostDTOMapper.mapToPostDTOs
import com.example.howtokotlin.controller.mapper.ReplyDTOMapper.mapToReplyDTO
import com.example.howtokotlin.controller.mapper.ReplyDTOMapper.mapToReplyDTOs
import com.example.howtokotlin.controller.mapper.UpdatePostDTOMapper
import com.example.howtokotlin.controller.mapper.UpdateReplyDTOMapper
import com.example.howtokotlin.controller.mapper.UpsertCategoryDTOMapper.mapToCategory
import com.example.howtokotlin.controller.model.*
import com.example.howtokotlin.controller.model.MessageDTO.Companion.fromId
import com.example.howtokotlin.exception.ForbiddenException
import com.example.howtokotlin.model.*
import com.example.howtokotlin.model.RoleName.ADMINISTRATOR
import com.example.howtokotlin.model.RoleName.MODERATOR
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import com.example.howtokotlin.service.AccountService
import com.example.howtokotlin.service.AuthService
import com.example.howtokotlin.service.ForumService
import org.springframework.stereotype.Component

@Component
class ForumAggregator(
    private val forumService: ForumService,
    private val accountService: AccountService,
    private val authService: AuthService,
) {
    fun updateCategory(
        categoryId: CategoryId,
        upsertCategoryDTO: UpsertCategoryDTO,
    ): CategoryDTO {
        val category: Category = forumService.getCategory(categoryId)
        return mapToCategoryDTO(forumService.updateCategory(mapToCategory(upsertCategoryDTO, category)))
    }

    fun getPosts(categoryId: CategoryId): List<PostDTO> {
        val posts: List<Post> = forumService.getPosts(categoryId)
        val accounts: List<Account> = accountService.getAccounts(getAccountIdsFromPosts(posts))

        return mapToPostDTOs(posts, accounts)
    }

    fun getPost(postId: PostId): PostDTO {
        val post: Post = forumService.getPost(postId)
        val account: Account = accountService.getAccount(post.accountId)

        return mapToPostDTO(post, listOf(account))
    }

    fun createPost(createPostDTO: CreatePostDTO): MessageDTO {
        val accountId: AccountId = authService.loggedInAccountId
        return fromId(forumService.createPost(mapToPost(createPostDTO, accountId)))
    }

    fun updatePost(
        postId: PostId,
        updatePostDTO: UpdatePostDTO,
    ): PostDTO {
        val post: Post = forumService.getPost(postId)
        checkAuthorized(post.accountId)
        val account: Account = accountService.getAccount(post.accountId)
        return mapToPostDTO(forumService.updatePost(UpdatePostDTOMapper.mapToPost(updatePostDTO, post)), listOf(account))
    }

    fun removePost(postId: PostId) {
        val post: Post = forumService.getPost(postId)
        checkAuthorized(post.accountId)
        forumService.removePost(postId)
    }

    fun getReplies(postId: PostId): List<ReplyDTO> {
        val replies: List<Reply> = forumService.getReplies(postId)
        val accounts: List<Account> = accountService.getAccounts(getAccountIdFromReplies(replies))

        return mapToReplyDTOs(replies, accounts)
    }

    fun getReply(replyId: ReplyId): ReplyDTO {
        val reply: Reply = forumService.getReply(replyId)
        val accounts: List<Account> = accountService.getAccounts(setOf(reply.accountId))
        return mapToReplyDTO(reply, accounts)
    }

    fun createReply(createReplyDTO: CreateReplyDTO): MessageDTO {
        val accountId: AccountId = authService.loggedInAccountId
        return fromId(forumService.createReply(CreateReplyDTOMapper.mapToReply(createReplyDTO, accountId)))
    }

    fun updateReply(
        replyId: ReplyId,
        updateReplyDTO: UpdateReplyDTO,
    ): ReplyDTO {
        val reply: Reply = forumService.getReply(replyId)
        checkAuthorized(reply.accountId)
        val account: Account = accountService.getAccount(reply.accountId)
        return mapToReplyDTO(
            forumService.updateReply(UpdateReplyDTOMapper.mapToReply(updateReplyDTO, reply)),
            listOf(account),
        )
    }

    fun removeReply(replyId: ReplyId) {
        val reply: Reply = forumService.getReply(replyId)
        checkAuthorized(reply.accountId)
        forumService.removeReply(replyId)
    }

    private fun checkAuthorized(accountId: AccountId) {
        if (accountId != authService.loggedInAccountId &&
            !authService.loggedInAccountHasAnyOf(
                listOf(
                    MODERATOR,
                    ADMINISTRATOR,
                ),
            )
        ) {
            throw ForbiddenException()
        }
    }

    private fun getAccountIdsFromPosts(posts: List<Post>): Set<AccountId> = posts.map { it.accountId }.toSet()

    private fun getAccountIdFromReplies(replies: List<Reply>): Set<AccountId> = replies.map { it.accountId }.toSet()
}
