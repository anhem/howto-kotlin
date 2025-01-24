package com.example.howtokotlin.controller

import com.example.howtokotlin.controller.model.*
import com.example.howtokotlin.testutil.TestApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class AccountControllerIT : TestApplication() {
    @Test
    fun crud() {
        val createAccountDTO =
            CreateAccountDTO(
                firstName = "testFirstName",
                lastName = "testLastName",
                email = "integration@example.com",
                username = "testUsername",
                password = "testPassword",
            )

        val createResponse: ResponseEntity<MessageDTO> =
            postWithToken(
                CREATE_USER_ACCOUNT_URL,
                createAccountDTO,
                MessageDTO::class.java,
                adminJwtToken,
            )

        assertThat(createResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(createResponse.body).isNotNull()

        val accountId: Int = createResponse.body!!.message.toInt()

        val accountDetailsResponse: ResponseEntity<AccountDetailsDTO> =
            getWithToken(
                String.format(GET_ACCOUNT_DETAILS_URL, accountId),
                AccountDetailsDTO::class.java,
                adminJwtToken,
            )
        assertThat(accountDetailsResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(accountDetailsResponse.body).isNotNull()
        assertAccount(accountDetailsResponse.body!!.account, accountId, createAccountDTO)

        val accountsResponse: ResponseEntity<AccountsDTO> =
            getWithToken(
                GET_ACCOUNTS_URL,
                AccountsDTO::class.java,
                adminJwtToken,
            )

        assertThat(accountsResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(accountsResponse.body).isNotNull
        val accountsDTO: AccountsDTO = accountsResponse.body!!
        assertThat(accountsDTO).isNotNull()
        assertThat(accountsDTO.accountCount).isEqualTo(accountsDTO.accounts.size)
        assertThat(accountsDTO.accounts).contains(accountDetailsResponse.body!!.account)

        val deleteResponse: ResponseEntity<MessageDTO> =
            deleteWithToken(
                String.format(DELETE_ACCOUNT_URL, accountId),
                MessageDTO::class.java,
                adminJwtToken,
            )

        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(deleteResponse.body).isNotNull()
        assertThat(deleteResponse.body).isEqualTo(MessageDTO.OK)
    }

    @Test
    fun userIsUnauthorized() {
        val createAccountDTO =
            CreateAccountDTO(
                firstName = "testFirstName",
                lastName = "testLastName",
                email = "integration@example.com",
                username = "testUsername",
                password = "testPassword",
            )

        val createResponse: ResponseEntity<MessageDTO> =
            postWithToken(
                CREATE_USER_ACCOUNT_URL,
                createAccountDTO,
                MessageDTO::class.java,
                userJwtToken,
            )

        assertThat(createResponse.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    companion object {
        private const val CREATE_USER_ACCOUNT_URL = "/api/accounts/users/user"
        private const val GET_ACCOUNT_DETAILS_URL = "/api/accounts/details/%d"
        private const val GET_ACCOUNTS_URL = "/api/accounts"
        const val DELETE_ACCOUNT_URL: String = "/api/accounts/%d"

        private fun assertAccount(
            accountDTO: AccountDTO,
            accountId: Int,
            createAccountDTO: CreateAccountDTO,
        ) {
            assertThat(accountDTO).isNotNull()
            assertThat(accountDTO.id).isEqualTo(accountId)
            assertThat(accountDTO.username).isEqualTo(createAccountDTO.username)
            assertThat(accountDTO.email).isEqualTo(createAccountDTO.email)
            assertThat(accountDTO.firstName).isEqualTo(createAccountDTO.firstName)
            assertThat(accountDTO.lastName).isEqualTo(createAccountDTO.lastName)
        }
    }
}
