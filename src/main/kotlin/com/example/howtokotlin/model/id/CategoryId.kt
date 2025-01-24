package com.example.howtokotlin.model.id

data class CategoryId(
    override val value: Int,
) : Id<Int> {
    companion object {
        val NEW_CATEGORY_ID: CategoryId = CategoryId(0)
    }
}
