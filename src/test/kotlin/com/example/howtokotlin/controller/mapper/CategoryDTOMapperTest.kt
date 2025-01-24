package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.CategoryDTOMapper.mapToCategoryDTOs
import com.example.howtokotlin.controller.model.CategoryDTO
import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.id.CategoryId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class CategoryDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val category =
            Category(
                categoryId = CategoryId(1),
                name = "name",
                description = "description",
                created = Instant.now(),
                lastUpdated = Instant.now(),
            )

        val categoryDTOs: List<CategoryDTO> = mapToCategoryDTOs(listOf(category))

        assertThat(categoryDTOs).hasSize(1)
        assertThat(categoryDTOs[0]).hasNoNullFieldsOrProperties()
    }
}
