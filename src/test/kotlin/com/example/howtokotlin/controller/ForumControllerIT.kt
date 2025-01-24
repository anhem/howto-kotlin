package com.example.howtokotlin.controller

import com.example.howtokotlin.client.urlhaus.UrlHausClient
import com.example.howtokotlin.controller.model.*
import com.example.howtokotlin.controller.model.ErrorCode.ACCESS_DENIED
import com.example.howtokotlin.controller.model.ErrorCode.VALIDATION_ERROR
import com.example.howtokotlin.model.id.JwtToken
import com.example.howtokotlin.service.ForumService.Companion.MALICIOUS_URL_DETECTED
import com.example.howtokotlin.testutil.TestApplication
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.ResponseEntity
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ForumControllerIT : TestApplication() {
    @TestConfiguration
    class ForumControllerTestConfig {
        @Bean
        fun urlHausClient() = mockk<UrlHausClient>()
    }

    @Autowired
    private lateinit var urlHausClient: UrlHausClient

    @Test
    fun categoryCrud() {
        val categoryId = createCategory()

        val categoryDTO: CategoryDTO = assertAndGetCategory(categoryId, true)!!
        assertThat(findCategoryDTO(categoryId, categories)).isNotNull

        val updatedCategoryDTO: CategoryDTO = updateCategory(categoryId)
        assertThat(updatedCategoryDTO.categoryId).isEqualTo(categoryDTO.categoryId)
        assertThat(updatedCategoryDTO).isNotEqualTo(categoryDTO)
        assertThat(updatedCategoryDTO).isEqualTo(assertAndGetCategory(categoryId, true)!!)

        deleteCategory(categoryId)

        assertAndGetCategory(categoryId, false)
        assertThat(findCategoryDTO(categoryId, categories)).isNull()
    }

    @Test
    fun postCrud() {
        val categoryId = createCategory()
        val postId = createPost(categoryId)

        val postDTO: PostDTO = assertAndGetPost(postId, true)!!
        assertThat(findPostDTO(postId, getPosts(categoryId))).isNotNull

        val updatedPostDTO: PostDTO = updatePost(postId)
        assertThat(updatedPostDTO.categoryId).isEqualTo(postDTO.categoryId)
        assertThat(updatedPostDTO).isNotEqualTo(postDTO)
        assertThat(updatedPostDTO).isEqualTo(assertAndGetPost(postId, true)!!)

        deletePost(postId)

        assertAndGetPost(postId, false)
        assertThat(findPostDTO(postId, getPosts(categoryId))).isNull()
    }

    @Test
    fun replyCrud() {
        val postId = createPost(createCategory())
        val replyId = createReply(postId)

        val replyDTO: ReplyDTO = assertAndGetReply(replyId, true)!!
        assertThat(findReplyDTO(replyId, getReplies(postId))).isNotNull

        val updatedReplyDTO: ReplyDTO = updateReply(replyId)
        assertThat(updatedReplyDTO.replyId).isEqualTo(replyDTO.replyId)
        assertThat(updatedReplyDTO).isNotEqualTo(replyDTO)
        assertThat(updatedReplyDTO).isEqualTo(assertAndGetReply(replyId, true)!!)

        deleteReply(replyId)

        assertAndGetReply(replyId, false)
        assertThat(findReplyDTO(replyId, getReplies(postId))).isNull()
    }

    @Test
    fun moderatorCanCreateButNotDeleteCategory() {
        val categoryId = createCategory(moderatorJwtToken)
        assertAndGetCategory(categoryId, true)

        val response: ResponseEntity<ErrorDTO> =
            deleteWithToken(
                String.format(CATEGORY_URL, categoryId),
                ErrorDTO::class.java,
                moderatorJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(FORBIDDEN)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.errorCode).isEqualTo(ACCESS_DENIED)
    }

    @Test
    fun userCannotCreateCategory() {
        val response: ResponseEntity<ErrorDTO> =
            postWithToken(
                GET_CATEGORIES_URL,
                UpsertCategoryDTO(name = "name", description = "description"),
                ErrorDTO::class.java,
                userJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(FORBIDDEN)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.errorCode).isEqualTo(ACCESS_DENIED)
    }

    @Test
    fun moderatorCanDeletePostMadeByUser() {
        val categoryId = createCategory()
        val postId = createPost(categoryId)

        deletePost(postId, moderatorJwtToken)

        assertAndGetPost(postId, false)
    }

    @Test
    fun moderatorCanDeleteReplyMadeByUser() {
        val replyId = createReply(createPost(createCategory()))

        deleteReply(replyId, moderatorJwtToken)

        assertAndGetReply(replyId, false)
    }

    @Test
    fun administratorCanDeletePostMadeByUser() {
        val categoryId = createCategory()
        val postId = createPost(categoryId)

        deletePost(postId, adminJwtToken)

        assertAndGetPost(postId, false)
    }

    @Test
    fun administratorCanDeleteReplyMadeByUser() {
        val replyId = createReply(createPost(createCategory()))

        deleteReply(replyId, adminJwtToken)

        assertAndGetReply(replyId, false)
    }

    @Test
    fun createPostWithMaliciousUrlReturnError() {
        val categoryId = createCategory()
        val createPostDTO = CreatePostDTO(categoryId = categoryId, title = MALICIOUS_URL, body = "body")
        every { urlHausClient.checkForMaliciousUrls(setOf(MALICIOUS_URL)) } returns true
        val response: ResponseEntity<ErrorDTO> =
            postWithToken(
                CREATE_POST_URL,
                createPostDTO,
                ErrorDTO::class.java,
                userJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.errorCode).isEqualTo(VALIDATION_ERROR)
        assertThat(response.body!!.message).isEqualTo(MALICIOUS_URL_DETECTED)
    }

    @Test
    fun updatePostWithMaliciousUrlReturnError() {
        val categoryId = createCategory()
        val postId = createPost(categoryId)
        val updatePostDTO = UpdatePostDTO(title = "title", body = MALICIOUS_URL)
        every { urlHausClient.checkForMaliciousUrls(setOf(MALICIOUS_URL)) } returns true

        val response: ResponseEntity<ErrorDTO> =
            putWithToken(
                String.format(POST_URL, postId),
                updatePostDTO,
                ErrorDTO::class.java,
                userJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.errorCode).isEqualTo(VALIDATION_ERROR)
        assertThat(response.body!!.message).isEqualTo(MALICIOUS_URL_DETECTED)
    }

    @Test
    fun createReplyWithMaliciousUrlReturnsError() {
        val categoryId = createCategory()
        val postId = createPost(categoryId)
        val createReplyDTO = CreateReplyDTO(postId = postId, body = MALICIOUS_URL)
        every { urlHausClient.checkForMaliciousUrls(setOf(MALICIOUS_URL)) } returns true

        val response: ResponseEntity<ErrorDTO> =
            postWithToken(
                CREATE_REPLY_URL,
                createReplyDTO,
                ErrorDTO::class.java,
                userJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.errorCode).isEqualTo(VALIDATION_ERROR)
        assertThat(response.body!!.message).isEqualTo(MALICIOUS_URL_DETECTED)
    }

    @Test
    fun updateReplyWithMaliciousUrlReturnsError() {
        val categoryId = createCategory()
        val postId = createPost(categoryId)
        val replyId = createReply(postId)
        val updateReplyDTO = UpdateReplyDTO(body = MALICIOUS_URL)
        every { urlHausClient.checkForMaliciousUrls(setOf(MALICIOUS_URL)) } returns true

        val response: ResponseEntity<ErrorDTO> =
            putWithToken(
                String.format(REPLY_URL, replyId),
                updateReplyDTO,
                ErrorDTO::class.java,
                userJwtToken,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.errorCode).isEqualTo(VALIDATION_ERROR)
        assertThat(response.body!!.message).isEqualTo(MALICIOUS_URL_DETECTED)
    }

    private val categories: List<CategoryDTO>
        get() {
            val response: ResponseEntity<Array<CategoryDTO>> =
                getWithToken(
                    GET_CATEGORIES_URL,
                    Array<CategoryDTO>::class.java,
                    userJwtToken,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            return response.body!!.toList()
        }

    private fun assertAndGetCategory(
        categoryId: Int,
        shouldExist: Boolean,
    ): CategoryDTO? {
        if (shouldExist) {
            val response: ResponseEntity<CategoryDTO> =
                getWithToken(
                    String.format(CATEGORY_URL, categoryId),
                    CategoryDTO::class.java,
                    userJwtToken,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            return response.body!!
        } else {
            val response: ResponseEntity<ErrorDTO> =
                getWithToken(
                    String.format(CATEGORY_URL, categoryId),
                    ErrorDTO::class.java,
                    userJwtToken,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
            assertThat(response.body).isNotNull()
            assertThat(response.body!!.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
            return null
        }
    }

    private fun updateCategory(categoryId: Int): CategoryDTO {
        val upsertCategoryDTO = UpsertCategoryDTO(name = UUID.randomUUID().toString(), description = "updatedDescription")
        val response: ResponseEntity<CategoryDTO> =
            putWithToken(
                String.format(CATEGORY_URL, categoryId),
                upsertCategoryDTO,
                CategoryDTO::class.java,
                adminJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        return response.body!!
    }

    private fun createCategory(jwtToken: JwtToken = adminJwtToken): Int {
        val response: ResponseEntity<MessageDTO> =
            postWithToken(
                GET_CATEGORIES_URL,
                UpsertCategoryDTO(name = UUID.randomUUID().toString(), description = "createDescription"),
                MessageDTO::class.java,
                jwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        val categoryId: Int = response.body!!.message.toInt()
        assertThat(categoryId).isGreaterThan(0)
        return categoryId
    }

    private fun deleteCategory(categoryId: Int) {
        val deleteResponse: ResponseEntity<MessageDTO> =
            deleteWithToken(
                String.format(CATEGORY_URL, categoryId),
                MessageDTO::class.java,
                adminJwtToken,
            )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)
    }

    private fun createPost(categoryId: Int): Int {
        val createPostDTO = CreatePostDTO(categoryId = categoryId, title = "createTitle", body = "createBody")
        val response: ResponseEntity<MessageDTO> =
            postWithToken(
                CREATE_POST_URL,
                createPostDTO,
                MessageDTO::class.java,
                userJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        val postId: Int = response.body!!.message.toInt()
        assertThat(postId).isGreaterThan(0)

        return postId
    }

    private fun updatePost(postId: Int): PostDTO {
        val updatePostDTO = UpdatePostDTO(title = "updateTitle", body = "updateBody")
        val response: ResponseEntity<PostDTO> =
            putWithToken(
                String.format(POST_URL, postId),
                updatePostDTO,
                PostDTO::class.java,
                userJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.updated).isNotNull()
        return response.body!!
    }

    private fun getPosts(categoryId: Int): List<PostDTO> {
        val response: ResponseEntity<Array<PostDTO>> =
            getWithToken(
                String.format(GET_POSTS_URL, categoryId),
                Array<PostDTO>::class.java,
                userJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        return response.body!!.asList()
    }

    private fun assertAndGetPost(
        postId: Int,
        shouldExist: Boolean,
    ): PostDTO? {
        if (shouldExist) {
            val response: ResponseEntity<PostDTO> =
                getWithToken(
                    String.format(POST_URL, postId),
                    PostDTO::class.java,
                    userJwtToken,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            return response.body!!
        } else {
            val response: ResponseEntity<ErrorDTO> =
                getWithToken(
                    String.format(POST_URL, postId),
                    ErrorDTO::class.java,
                    userJwtToken,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
            assertThat(response.body).isNotNull()
            assertThat(response.body!!.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
            return null
        }
    }

    private fun deletePost(
        postId: Int,
        jwtToken: JwtToken = userJwtToken,
    ) {
        val deleteResponse: ResponseEntity<MessageDTO> =
            deleteWithToken(
                String.format(POST_URL, postId),
                MessageDTO::class.java,
                jwtToken,
            )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)
    }

    private fun createReply(postId: Int): Int {
        val createReplyDTO = CreateReplyDTO(postId = postId, body = "createBody")
        val response: ResponseEntity<MessageDTO> =
            postWithToken(
                String.format(CREATE_REPLY_URL, postId),
                createReplyDTO,
                MessageDTO::class.java,
                userJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        val replyId: Int = response.body!!.message.toInt()
        assertThat(replyId).isGreaterThan(0)
        return replyId
    }

    private fun updateReply(replyId: Int): ReplyDTO {
        val updateReplyDTO = UpdateReplyDTO(body = "updateBody")
        val response: ResponseEntity<ReplyDTO> =
            putWithToken(
                String.format(REPLY_URL, replyId),
                updateReplyDTO,
                ReplyDTO::class.java,
                userJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.updated).isNotNull()
        return response.body!!
    }

    private fun getReplies(postId: Int): List<ReplyDTO> {
        val response: ResponseEntity<Array<ReplyDTO>> =
            getWithToken(
                String.format(GET_REPLIES_URL, postId),
                Array<ReplyDTO>::class.java,
                userJwtToken,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        return response.body!!.toList()
    }

    private fun assertAndGetReply(
        replyId: Int,
        shouldExist: Boolean,
    ): ReplyDTO? {
        if (shouldExist) {
            val response: ResponseEntity<ReplyDTO> =
                getWithToken(
                    String.format(REPLY_URL, replyId),
                    ReplyDTO::class.java,
                    userJwtToken,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            return response.body
        } else {
            val response: ResponseEntity<ErrorDTO> =
                getWithToken(
                    String.format(REPLY_URL, replyId),
                    ErrorDTO::class.java,
                    userJwtToken,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
            assertThat(response.body).isNotNull()
            assertThat(response.body!!.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
            return null
        }
    }

    private fun deleteReply(
        replyId: Int,
        jwtToken: JwtToken = userJwtToken,
    ) {
        val deleteResponse: ResponseEntity<MessageDTO> =
            deleteWithToken(
                String.format(REPLY_URL, replyId),
                MessageDTO::class.java,
                jwtToken,
            )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)
    }

    companion object {
        private const val GET_CATEGORIES_URL = "/api/forum/categories"
        private const val CATEGORY_URL = "/api/forum/categories/%d"
        private const val CREATE_POST_URL = "/api/forum/posts"
        private const val GET_POSTS_URL = "/api/forum/categories/%d/posts"
        private const val POST_URL = "/api/forum/posts/%d"
        private const val CREATE_REPLY_URL = "/api/forum/replies"
        private const val GET_REPLIES_URL = "/api/forum/posts/%d/replies"
        private const val REPLY_URL = "/api/forum/replies/%d"
        private const val MALICIOUS_URL = "https://local.home.arpa/"

        private fun findCategoryDTO(
            categoryId: Int,
            categoryDTOs: List<CategoryDTO>,
        ): CategoryDTO? = categoryDTOs.firstOrNull { it.categoryId == categoryId }

        private fun findPostDTO(
            postId: Int,
            postDTOs: List<PostDTO>,
        ): PostDTO? = postDTOs.firstOrNull { it.postId == postId }

        private fun findReplyDTO(
            replyId: Int,
            replyDTOs: List<ReplyDTO>,
        ): ReplyDTO? = replyDTOs.firstOrNull { it.replyId == replyId }
    }
}
