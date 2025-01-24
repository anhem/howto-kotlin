package com.example.howtokotlin.repository

import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.repository.mapper.PostMapper.mapToPost
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
class PostRepository protected constructor(
    namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : JdbcRepository(namedParameterJdbcTemplate) {
    companion object {
        private const val SELECT_POSTS_BY_CATEGORY_ID = "SELECT * FROM post WHERE category_id = :categoryId"
        private const val SELECT_POST_BY_ID = "SELECT * FROM post WHERE post_id = :id"
        private const val DELETE_POST = "DELETE FROM post WHERE post_id = :postId"
        private const val INSERT_POST =
            "INSERT INTO post(category_id, account_id, title, body, created, last_updated) VALUES (:categoryId, :accountId, :title, :body, :created, :lastUpdated)"
        private const val UPDATE_POST = "UPDATE post SET title = :title, body = :body, last_updated = :lastUpdated WHERE post_id = :postId"
    }

    fun getPosts(categoryId: CategoryId): List<Post> {
        val parameters = createParameters("categoryId", categoryId.value)
        return namedParameterJdbcTemplate.query(SELECT_POSTS_BY_CATEGORY_ID, parameters) { rs, _ -> mapToPost(rs) }
    }

    fun getPost(postId: PostId): Post = findById(postId, SELECT_POST_BY_ID) { rs, _ -> mapToPost(rs) }

    fun createPost(post: Post): PostId {
        val parameters =
            createParameters("postId", post.postId.value)
                .addValue("categoryId", post.categoryId.value)
                .addValue("accountId", post.accountId.value)
                .addValue("title", post.title)
                .addValue("body", post.body)
                .addValue("created", Timestamp.from(post.created))
                .addValue("lastUpdated", Timestamp.from(post.lastUpdated))
        return insert(INSERT_POST, parameters, PostId::class.java)
    }

    fun updatePost(post: Post) {
        val parameters =
            createParameters("postId", post.postId.value)
                .addValue("title", post.title)
                .addValue("body", post.body)
                .addValue("lastUpdated", Timestamp.from(post.lastUpdated))
        namedParameterJdbcTemplate.update(UPDATE_POST, parameters)
    }

    fun removePost(postId: PostId) {
        val parameters = createParameters("postId", postId.value)
        namedParameterJdbcTemplate.update(DELETE_POST, parameters)
        log.info("Post {} removed", postId)
    }
}
