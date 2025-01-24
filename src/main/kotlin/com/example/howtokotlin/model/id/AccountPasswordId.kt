package com.example.howtokotlin.model.id

data class AccountPasswordId(
    override val value: Int,
) : Id<Int> {
    companion object {
        val NEW_ACCOUNT_PASSWORD_ID: AccountPasswordId = AccountPasswordId(0)
    }
}
