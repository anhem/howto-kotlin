package com.example.howtokotlin.repository.mapper

import com.example.howtokotlin.model.Account
import com.example.howtokotlin.model.id.AccountId
import com.example.howtokotlin.model.id.Username
import java.sql.ResultSet

object AccountMapper {
    fun mapToAccount(rs: ResultSet): Account =
        Account(
            accountId = AccountId(rs.getInt("account_id")),
            username = Username(rs.getString("username")),
            email = rs.getString("email"),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name"),
            created = rs.getTimestamp("created").toInstant(),
            lastUpdated = rs.getTimestamp("last_updated").toInstant(),
            lastLogin = rs.getTimestamp("last_login")?.toInstant(),
        )
}
