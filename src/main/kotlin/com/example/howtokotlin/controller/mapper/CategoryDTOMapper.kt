package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.model.CategoryDTO
import com.example.howtokotlin.model.Category

object CategoryDTOMapper {
    fun mapToCategoryDTOs(categories: List<Category>): List<CategoryDTO> = categories.map { mapToCategoryDTO(it) }

    fun mapToCategoryDTO(category: Category): CategoryDTO =
        CategoryDTO(
            categoryId = category.categoryId.value,
            name = category.name,
            description = category.description,
        )
}
