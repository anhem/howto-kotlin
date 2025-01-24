package com.example.howtokotlin.model.id

data class PostId(
    override val value: Int,
) : Id<Int> {
    companion object {
        val NEW_POST_ID: PostId = PostId(0)
    }
}
