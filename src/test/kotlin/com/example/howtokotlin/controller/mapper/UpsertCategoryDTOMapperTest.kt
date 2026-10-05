package com.example.howtokotlin.controller.mapper

import com.example.howtokotlin.controller.mapper.UpsertCategoryDTOMapper.mapToCategory
import com.example.howtokotlin.controller.model.UpsertCategoryDTO
import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.model.id.CategoryId.Companion.NEW_CATEGORY_ID
import com.example.howtokotlin.testutil.TestPopulator.populate
import com.github.anhem.testpopulator.config.OverridePopulate
import com.github.anhem.testpopulator.config.OverrideTarget
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UpsertCategoryDTOMapperTest {
    @Test
    fun mappedToNewModel() {
        val upsertCategoryDTO = populate<UpsertCategoryDTO>()

        val category: Category = mapToCategory(upsertCategoryDTO)

        assertThat(category).hasNoNullFieldsOrProperties()
        assertThat(category.categoryId).isEqualTo(NEW_CATEGORY_ID)
    }

    @Test
    fun mappedToExistingModel() {
        val upsertCategoryDTO = populate<UpsertCategoryDTO>(mapOf(
            OverrideTarget.of("name", String::class.java) to OverridePopulate { "name2" },
            OverrideTarget.of("description", String::class.java) to OverridePopulate { "description2" }
        ))
        val category = populate<Category>(mapOf(
            CategoryId::class.java to OverridePopulate { CategoryId(1) }
        ))

        val updatedCategory: Category = mapToCategory(upsertCategoryDTO, category)

        assertThat(updatedCategory).hasNoNullFieldsOrProperties()
    }
}
