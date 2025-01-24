package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.CreateReplyDTO
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.AccountId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CreateReplyDTOMapperTest {
    @Test
    fun mappedToModel() {
        val createReplyDTO =
            CreateReplyDTO(
                postId = 1,
                body = "body",
            )
        val accountId = AccountId(1)

        val reply: Reply = CreateReplyDTOMapper.mapToReply(createReplyDTO, accountId)

        assertThat(reply).hasNoNullFieldsOrProperties()
    }
}
