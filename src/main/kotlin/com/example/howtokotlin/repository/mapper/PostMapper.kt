package com.example.howtokotlin.repository.mapper

import com.example.howtokotlin.model.Post
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.PostId
import java.sql.ResultSet

object PostMapper {
    fun mapToPost(rs: ResultSet): Post =
        Post(
            postId = PostId(rs.getInt("post_id")),
            categoryId = CategoryId(rs.getInt("category_id")),
            accountId = AccountId(rs.getInt("account_id")),
            title = rs.getString("title"),
            body = rs.getString("body"),
            created = rs.getTimestamp("created").toInstant(),
            lastUpdated = rs.getTimestamp("last_updated").toInstant(),
        )
}
