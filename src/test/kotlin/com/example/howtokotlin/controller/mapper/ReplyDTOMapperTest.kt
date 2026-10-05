package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.ReplyDTOMapper.mapToReplyDTOs
import com.example.howtokotlin.controller.model.ReplyDTO
import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import com.example.howtokotlin.model.id.Username
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

internal class ReplyDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val account = populate<Account>(mapOf(
            AccountId::class.java to OverridePopulate { AccountId(1) },
            Username::class.java to OverridePopulate { Username("username") }
        ))
        val reply = populate<Reply>(mapOf(
            ReplyId::class.java to OverridePopulate { ReplyId(2) },
            PostId::class.java to OverridePopulate { PostId(3) },
            AccountId::class.java to OverridePopulate { account.accountId }
        ))

        val replyDTOs: List<ReplyDTO> = mapToReplyDTOs(listOf(reply), listOf(account))

        assertThat(replyDTOs).hasSize(1)
        assertThat(replyDTOs[0]).hasNoNullFieldsOrProperties()
    }
}
