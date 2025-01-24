package com.example.howtokotlin.service

import com.example.howtokotlin.client.urlhaus.UrlHausClient
import com.example.howtokotlin.exception.ValidationException
import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import com.example.howtokotlin.repository.CategoryRepository
import com.example.howtokotlin.repository.PostRepository
import com.example.howtokotlin.repository.ReplyRepository
import com.linkedin.urls.detection.UrlDetector
import com.linkedin.urls.detection.UrlDetectorOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ForumService(
    private val categoryRepository: CategoryRepository,
    private val postRepository: PostRepository,
    private val replyRepository: ReplyRepository,
    private val urlHausClient: UrlHausClient,
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun getCategories(): List<Category> = categoryRepository.getCategories()

    fun getCategory(categoryId: CategoryId): Category = categoryRepository.getCategory(categoryId)

    @Transactional
    fun removeCategory(categoryId: CategoryId) {
        val category: Category = categoryRepository.getCategory(categoryId)
        categoryRepository.removeCategory(categoryId)
        log.info("{} removed", category)
    }

    @Transactional
    fun createCategory(category: Category): CategoryId = categoryRepository.createCategory(category)

    @Transactional
    fun updateCategory(category: Category): Category {
        categoryRepository.updateCategory(category)
        return categoryRepository.getCategory(category.categoryId)
    }

    fun getPost(postId: PostId): Post = postRepository.getPost(postId)

    fun getPosts(categoryId: CategoryId): List<Post> = postRepository.getPosts(categoryId)

    @Transactional
    fun createPost(post: Post): PostId {
        if (!post.postId.isNew()) {
            throw ValidationException("Invalid postId")
        }
        checkForMaliciousUrls(post)
        return postRepository.createPost(post)
    }

    @Transactional
    fun updatePost(post: Post): Post {
        checkForMaliciousUrls(post)
        postRepository.updatePost(post)
        return postRepository.getPost(post.postId)
    }

    @Transactional
    fun removePost(postId: PostId) {
        val post: Post = postRepository.getPost(postId)
        replyRepository.removeReplies(postId)
        postRepository.removePost(postId)
        log.info("{} and replies removed", post)
    }

    fun getReplies(postId: PostId): List<Reply> = replyRepository.getReplies(postId)

    fun getReply(replyId: ReplyId): Reply = replyRepository.getReply(replyId)

    @Transactional
    fun createReply(reply: Reply): ReplyId {
        checkForMaliciousUrls(reply)
        return replyRepository.createReply(reply)
    }

    @Transactional
    fun updateReply(reply: Reply): Reply {
        checkForMaliciousUrls(reply)
        replyRepository.updateReply(reply)
        return replyRepository.getReply(reply.replyId)
    }

    @Transactional
    fun removeReply(replyId: ReplyId) {
        val reply: Reply = replyRepository.getReply(replyId)
        replyRepository.removeReply(replyId)
        log.info("{} removed", reply)
    }

    private fun checkForMaliciousUrls(post: Post) {
        checkForMaliciousUrls(detectUrls(post.title, post.body))
    }

    private fun checkForMaliciousUrls(reply: Reply) {
        checkForMaliciousUrls(detectUrls(reply.body))
    }

    private fun checkForMaliciousUrls(urls: Set<String>) {
        if (urls.isNotEmpty()) {
            if (urlHausClient.checkForMaliciousUrls(urls)) {
                throw ValidationException(MALICIOUS_URL_DETECTED)
            }
        }
    }

    companion object {
        const val MALICIOUS_URL_DETECTED: String = "Malicious url detected!"

        private fun detectUrls(vararg strings: String?): Set<String> =
            strings
                .filterNotNull()
                .flatMap { UrlDetector(it, UrlDetectorOptions.Default).detect() }
                .map { it.fullUrl }
                .toSet()
    }
}
