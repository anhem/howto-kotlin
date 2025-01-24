package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.ReplyDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.model.id.Username.Companion.UNKNOWN

object ReplyDTOMapper {
    fun mapToReplyDTOs(
        replies: List<Reply>,
        accounts: List<Account>,
    ): List<ReplyDTO> = replies.map { mapToReplyDTO(it, accounts) }

    fun mapToReplyDTO(
        reply: Reply,
        accounts: List<Account>,
    ): ReplyDTO {
        val username: Username =
            accounts
                .firstOrNull { it.accountId == reply.accountId }
                ?.username ?: UNKNOWN

        return ReplyDTO(
            replyId = reply.replyId.value,
            postId = reply.postId.value,
            username = username.value,
            body = reply.body,
            created = reply.created,
            updated = reply.lastUpdated,
        )
    }
}
