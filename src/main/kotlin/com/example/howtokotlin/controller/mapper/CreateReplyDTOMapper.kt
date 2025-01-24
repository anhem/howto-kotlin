package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.CreateReplyDTO
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import java.time.Instant

object CreateReplyDTOMapper {
    fun mapToReply(
        createReplyDTO: CreateReplyDTO,
        accountId: AccountId,
    ): Reply {
        val now = Instant.now()
        return Reply(
            replyId = ReplyId.NEW_REPLY_ID,
            postId = PostId(createReplyDTO.postId),
            accountId = accountId,
            body = createReplyDTO.body,
            created = now,
            lastUpdated = now,
        )
    }
}
