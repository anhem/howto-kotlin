package com.example.howtokotlin.repository.mapper

import com.example.howtokotlin.model.Category
import com.example.howtokotlin.model.id.CategoryId
import java.sql.ResultSet

object CategoryMapper {
    fun mapToCategory(rs: ResultSet): Category =
        Category(
            categoryId = CategoryId(rs.getInt("category_id")),
            name = rs.getString("name"),
            description = rs.getString("description"),
            created = rs.getTimestamp("created").toInstant(),
            lastUpdated = rs.getTimestamp("last_updated").toInstant(),
        )
}
