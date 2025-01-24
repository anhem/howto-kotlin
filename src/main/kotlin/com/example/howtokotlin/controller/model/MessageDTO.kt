package com.example.howtokotlin.controller.model

import com.example.howtokotlin.model.id.Id

data class MessageDTO(
    val message: String,
) {
    companion object {
        val OK: MessageDTO = MessageDTO("OK")

        fun fromId(id: Id<*>): MessageDTO = MessageDTO(id.value.toString())
    }
}
