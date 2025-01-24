package com.example.howtokotlin.model.id

data class AccountId(
    override val value: Int,
) : Id<Int> {
    companion object {
        val NEW_ACCOUNT_ID: AccountId = AccountId(0)
    }
}
