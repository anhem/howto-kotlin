package com.example.howtokotlin.repository

import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import com.example.howtokotlin.repository.mapper.ReplyMapper.mapToReply
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
class ReplyRepository(
    namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : JdbcRepository(namedParameterJdbcTemplate) {
    companion object {
        private const val SELECT_REPLIES_BY_POST_ID = "SELECT * FROM reply where post_id = :postId"
        private const val SELECT_POST_BY_ID = "SELECT * FROM reply WHERE reply_id = :id"
        private const val DELETE_REPLY = "DELETE FROM reply WHERE reply_id = :replyId"
        private const val DELETE_REPLIES = "DELETE FROM reply WHERE post_id = :postId"
        private const val INSERT_REPLY =
            "INSERT INTO reply(post_id, account_id, body, created, last_updated) VALUES (:postId, :accountId, :body, :created, :lastUpdated)"
        private const val UPDATE_REPLY = "UPDATE REPLY SET body = :body, last_updated = :lastUpdated WHERE reply_id = :replyId"
    }

    fun getReplies(postId: PostId): List<Reply> {
        val parameters = createParameters("postId", postId.value)
        return namedParameterJdbcTemplate.query(SELECT_REPLIES_BY_POST_ID, parameters) { rs, _ -> mapToReply(rs) }
    }

    fun getReply(replyId: ReplyId): Reply = findById(replyId, SELECT_POST_BY_ID) { rs, _ -> mapToReply(rs) }

    fun createReply(reply: Reply): ReplyId {
        val parameters =
            createParameters("postId", reply.postId.value)
                .addValue("accountId", reply.accountId.value)
                .addValue("body", reply.body)
                .addValue("created", Timestamp.from(reply.created))
                .addValue("lastUpdated", Timestamp.from(reply.lastUpdated))
        return insert(INSERT_REPLY, parameters, ReplyId::class.java)
    }

    fun updateReply(reply: Reply) {
        val parameters =
            createParameters("replyId", reply.replyId.value)
                .addValue("body", reply.body)
                .addValue("lastUpdated", Timestamp.from(reply.lastUpdated))
        namedParameterJdbcTemplate.update(UPDATE_REPLY, parameters)
    }

    fun removeReply(ReplyId: ReplyId) {
        val parameters = createParameters("replyId", ReplyId.value)
        namedParameterJdbcTemplate.update(DELETE_REPLY, parameters)
        log.info("Reply {} removed", ReplyId)
    }

    fun removeReplies(postId: PostId) {
        val parameters = createParameters("postId", postId.value)
        namedParameterJdbcTemplate.update(DELETE_REPLIES, parameters)
        log.info("Replies for {} removed", postId)
    }
}
