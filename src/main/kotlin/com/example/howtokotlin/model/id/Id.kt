package com.example.howtokotlin.model.id

interface Id<T> {
    companion object {
        const val NEW_INT: Int = 0
    }

    val value: Any

    fun isNew(): Boolean {
        val value: Any = value
        if (value is Int) {
            return value == NEW_INT
        }
        throw IllegalStateException(String.format("missing implementation for %s", value.javaClass.getSimpleName()))
    }
}
