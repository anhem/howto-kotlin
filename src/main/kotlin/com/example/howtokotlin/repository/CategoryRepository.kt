package com.example.howtokotlin.repository

import com.example.howtokotlin.exception.NotFoundException
import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.id.CategoryId
import com.example.howtokotlin.repository.mapper.CategoryMapper.mapToCategory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
class CategoryRepository(
    namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : JdbcRepository(namedParameterJdbcTemplate) {
    companion object {
        private const val SELECT_CATEGORIES = "SELECT * FROM category"
        private const val SELECT_CATEGORY_BY_ID = "SELECT * FROM category WHERE category_id = :id"
        private const val DELETE_CATEGORY = "DELETE FROM category WHERE category_id = :categoryId"
        private const val INSERT_CATEGORY =
            "INSERT INTO category(name, description, created, last_updated) values(:name, :description, :created, :lastUpdated)"
        private const val UPDATE_CATEGORY =
            "UPDATE category SET name = :name, description = :description, last_updated = :lastUpdated WHERE category_id = :categoryId"
    }

    fun getCategories(): List<Category> = namedParameterJdbcTemplate.query(SELECT_CATEGORIES) { rs, _ -> mapToCategory(rs) }

    fun getCategory(categoryId: CategoryId): Category {
        return findById(categoryId, SELECT_CATEGORY_BY_ID) { rs, _ -> mapToCategory(rs) }
    }

    fun createCategory(category: Category): CategoryId {
        val parameters =
            createParameters("categoryId", category.categoryId.value)
                .addValue("name", category.name)
                .addValue("description", category.description)
                .addValue("created", Timestamp.from(category.created))
                .addValue("lastUpdated", Timestamp.from(category.lastUpdated))
        return insert(INSERT_CATEGORY, parameters, CategoryId::class.java)
    }

    fun updateCategory(category: Category) {
        val parameters =
            createParameters("categoryId", category.categoryId.value)
                .addValue("name", category.name)
                .addValue("description", category.description)
                .addValue("lastUpdated", Timestamp.from(category.lastUpdated))
        namedParameterJdbcTemplate.update(UPDATE_CATEGORY, parameters)
    }

    fun removeCategory(categoryId: CategoryId) {
        val parameters = createParameters("categoryId", categoryId.value)
        namedParameterJdbcTemplate.update(DELETE_CATEGORY, parameters)
        log.info("Category {} removed", categoryId)
    }
}
