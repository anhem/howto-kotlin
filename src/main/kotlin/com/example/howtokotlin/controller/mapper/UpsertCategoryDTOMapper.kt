package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.UpsertCategoryDTO
import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.id.CategoryId
import java.time.Instant

object UpsertCategoryDTOMapper {
    fun mapToCategory(upsertCategoryDTO: UpsertCategoryDTO): Category {
        val now = Instant.now()
        return Category(
            categoryId = CategoryId.NEW_CATEGORY_ID,
            name = upsertCategoryDTO.name,
            description = upsertCategoryDTO.description,
            created = now,
            lastUpdated = now,
        )
    }

    fun mapToCategory(
        upsertCategoryDTO: UpsertCategoryDTO,
        category: Category,
    ): Category =
        category.copy(
            name = upsertCategoryDTO.name,
            description = upsertCategoryDTO.description,
            lastUpdated = Instant.now(),
        )
}
