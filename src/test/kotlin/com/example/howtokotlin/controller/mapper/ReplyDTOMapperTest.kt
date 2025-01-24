package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.ReplyDTOMapper.mapToReplyDTOs
import com.example.howtokotlin.controller.model.ReplyDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import com.example.howtokotlin.model.id.Username
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class ReplyDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val account =
            Account(
                accountId = AccountId(1),
                username = Username("username"),
                firstName = "firstName",
                lastName = "lastName",
                email = "email",
                created = Instant.now(),
                lastUpdated = Instant.now(),
                lastLogin = Instant.now(),
            )
        val reply =
            Reply(
                replyId = ReplyId(2),
                postId = PostId(3),
                accountId = account.accountId,
                body = "body",
                created = Instant.now(),
                lastUpdated = Instant.now(),
            )

        val replyDTOs: List<ReplyDTO> = mapToReplyDTOs(listOf(reply), listOf(account))

        assertThat(replyDTOs).hasSize(1)
        assertThat(replyDTOs[0]).hasNoNullFieldsOrProperties()
    }
}
