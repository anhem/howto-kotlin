package com.example.howtokotlin.model.id

data class ReplyId(
    override val value: Int,
) : Id<Int> {
    companion object {
        val NEW_REPLY_ID: ReplyId = ReplyId(0)
    }
}
