package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.UpsertCategoryDTOMapper.mapToCategory
import com.example.howtokotlin.controller.model.UpsertCategoryDTO
import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.CategoryId.Companion.NEW_CATEGORY_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class UpsertCategoryDTOMapperTest {
    @Test
    fun mappedToNewModel() {
        val upsertCategoryDTO =
            UpsertCategoryDTO(
                name = "name",
                description = "description",
            )

        val category: Category = mapToCategory(upsertCategoryDTO)

        assertThat(category).hasNoNullFieldsOrProperties()
        assertThat(category.categoryId).isEqualTo(NEW_CATEGORY_ID)
    }

    @Test
    fun mappedToExistingModel() {
        val upsertCategoryDTO =
            UpsertCategoryDTO(
                name = "name2",
                description = "description2",
            )
        val category =
            Category(
                categoryId = CategoryId(1),
                name = "name",
                description = "description",
                created = Instant.now(),
                lastUpdated = Instant.now(),
            )

        val updatedCategory: Category = mapToCategory(upsertCategoryDTO, category)

        assertThat(updatedCategory).hasNoNullFieldsOrProperties()
    }
}
