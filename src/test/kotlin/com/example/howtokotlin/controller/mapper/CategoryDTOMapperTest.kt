package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.CategoryDTOMapper.mapToCategoryDTOs
import com.example.howtokotlin.controller.model.CategoryDTO
import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CategoryDTOMapperTest {
    @Test
    fun mappedToDTO() {
        val category = populate<Category>(mapOf(
            CategoryId::class.java to OverridePopulate { CategoryId(1) }
        ))

        val categoryDTOs: List<CategoryDTO> = mapToCategoryDTOs(listOf(category))

        assertThat(categoryDTOs).hasSize(1)
        assertThat(categoryDTOs[0]).hasNoNullFieldsOrProperties()
    }
}
