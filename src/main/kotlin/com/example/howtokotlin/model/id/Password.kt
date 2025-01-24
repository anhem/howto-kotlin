package com.example.howtokotlin.model.id

data class Password(
    override val value: String,
) : Id<String> {
    companion object {
        const val BCRYPT: String = "{bcrypt}"
    }

    init {
        if (!value.startsWith(BCRYPT)) {
            throw RuntimeException("Password not encoded!")
        }
    }
}
