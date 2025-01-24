package com.example.howtokotlin.repository.mapper

import com.example.howtokotlin.model.AccountPassword
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.AccountPasswordId
import com.example.howtokotlin.model.id.Password
import java.sql.ResultSet

object AccountPasswordMapper {
    fun mapToAccountPassword(rs: ResultSet): AccountPassword =
        AccountPassword(
            accountPasswordId = AccountPasswordId(rs.getInt("account_password_id")),
            accountId = AccountId(rs.getInt("account_id")),
            password = Password(rs.getString("password")),
            created = rs.getTimestamp("created").toInstant(),
        )
}
