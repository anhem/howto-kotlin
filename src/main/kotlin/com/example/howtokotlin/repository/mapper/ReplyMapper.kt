package com.example.howtokotlin.repository.mapper

import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import java.sql.ResultSet

object ReplyMapper {
    fun mapToReply(rs: ResultSet): Reply =
        Reply(
            replyId = ReplyId(rs.getInt("reply_id")),
            postId = PostId(rs.getInt("post_id")),
            accountId = AccountId(rs.getInt("account_id")),
            body = rs.getString("body"),
            created = rs.getTimestamp("created").toInstant(),
            lastUpdated = rs.getTimestamp("last_updated").toInstant(),
        )
}
