package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.UpdateReplyDTOMapper.mapToReply
import com.example.howtokotlin.controller.model.UpdateReplyDTO
import com.example.howtokotlin.model.Reply
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.PostId
import com.example.howtokotlin.model.id.ReplyId
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UpdateReplyDTOMapperTest {
    @Test
    fun mappedToModel() {
        val updateReplyDTO = populate<UpdateReplyDTO>("body", String::class.java) { "body2" }
        val reply = populate<Reply>(mapOf(
            ReplyId::class.java to OverridePopulate { ReplyId(1) },
            PostId::class.java to OverridePopulate { PostId(2) },
            AccountId::class.java to OverridePopulate { AccountId(3) },
            OverrideTarget.of("lastUpdated", java.time.Instant::class.java) to OverridePopulate { java.time.Instant.now().minusSeconds(10) }
        ))

        val updatedReply: Reply = mapToReply(updateReplyDTO, reply)

        assertThat(updatedReply).hasNoNullFieldsOrProperties()
        assertThat(updatedReply.body).isEqualTo(updateReplyDTO.body)
        assertThat(updatedReply.lastUpdated).isAfter(reply.lastUpdated)
        assertThat(updatedReply).usingRecursiveComparison()
            .ignoringFields("body", "lastUpdated")
            .isEqualTo(reply)
    }
}
