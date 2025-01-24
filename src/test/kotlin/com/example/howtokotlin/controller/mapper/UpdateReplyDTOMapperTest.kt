package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.UpdateReplyDTOMapper.mapToReply
import com.example.howtokotlin.controller.model.UpdateReplyDTO
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class UpdateReplyDTOMapperTest {
    @Test
    fun mappedToModel() {
        val updateReplyDTO =
            UpdateReplyDTO(
                body = "body2",
            )
        val reply =
            Reply(
                replyId = ReplyId(1),
                postId = PostId(2),
                accountId = AccountId(3),
                body = "body",
                created = Instant.now(),
                lastUpdated = Instant.now(),
            )

        val updatedReply: Reply = mapToReply(updateReplyDTO, reply)

        assertThat(updatedReply).hasNoNullFieldsOrProperties()
        assertThat(updatedReply).isNotEqualTo(reply)
    }
}
