package com.example.howtokotlin.repository

import com.example.howtokotlin.exception.NotFoundException
import com.example.howtokotlin.model.id.Id
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import java.util.*

abstract class JdbcRepository protected constructor(
    protected val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) {
    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    protected fun createParameters(
        name: String,
        value: Any?,
    ): MapSqlParameterSource = MapSqlParameterSource().addValue(name, value)

    protected fun <T> findById(
        id: Id<*>,
        sql: String,
        rowMapper: RowMapper<T>,
    ): T {
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, createParameters("id", id.value), rowMapper)
                ?: throw NotFoundException(id)
        } catch (e: EmptyResultDataAccessException) {
            throw NotFoundException(id)
        }
    }

    protected fun <T : Id<Int>> insert(
        sql: String,
        parameters: MapSqlParameterSource,
        returnClass: Class<T>,
    ): T {
        try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            val primaryKeyName = getPrimaryKeyName(returnClass)
            namedParameterJdbcTemplate.update(sql, parameters, keyHolder, arrayOf(primaryKeyName))
            val t = returnClass.getDeclaredConstructor(Int::class.java).newInstance(extractNumberId(keyHolder))
            log.info("{} created", t)
            return t
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun <T : Id<Int>> getPrimaryKeyName(returnClass: Class<T>): String =
        returnClass.simpleName
            .replace("(?!^)([A-Z])".toRegex(), "_$1")
            .lowercase(Locale.getDefault())

    private fun extractNumberId(keyHolder: KeyHolder): Int =
        Optional
            .ofNullable(keyHolder.key)
            .map { obj: Number -> obj.toInt() }
            .orElseThrow { RuntimeException("Failed to extract id") }
}
