package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.UpdateReplyDTO
import com.example.howtokotlin.model.Reply
import java.time.Instant

object UpdateReplyDTOMapper {
    fun mapToReply(
        updateReplyDTO: UpdateReplyDTO,
        reply: Reply,
    ): Reply =
        reply.copy(
            body = updateReplyDTO.body,
            lastUpdated = Instant.now(),
        )
}
