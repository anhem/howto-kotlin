package com.example.howtokotlin.controller

import com.example.howtokotlin.controller.ControllerAdvice.Companion.DENIED_ERROR_MESSAGE
import com.example.howtokotlin.controller.ControllerAdvice.Companion.NOT_FOUND_ERROR_MESSAGE
import com.example.howtokotlin.controller.ControllerAdvice.Companion.UNEXPECTED_ERROR_MESSAGE
import com.example.howtokotlin.controller.ControllerAdvice.Companion.VALIDATION_ERROR_MESSAGE
import com.example.howtokotlin.controller.model.ErrorCode.*
import com.example.howtokotlin.controller.model.ErrorDTO
import com.example.howtokotlin.exception.ForbiddenException
import com.example.howtokotlin.exception.NotFoundException
import com.example.howtokotlin.exception.ValidationException
import com.example.howtokotlin.model.id.AccountId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.MethodParameter
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest

@ExtendWith(MockKExtension::class)
class ControllerAdviceTest {
    companion object {
        private const val USERNAME_FIELD = "username"
        private const val PASSWORD_FIELD = "password"
        private const val USERNAME_ERROR_MESSAGE_1 = "username error message 1"
        private const val USERNAME_ERROR_MESSAGE_2 = "username error message 2"
        private const val PASSWORD_ERROR_MESSAGE_1 = "password error message 1"
    }

    private val controllerAdvice = ControllerAdvice()

    @MockK
    private lateinit var webRequest: WebRequest

    @MockK
    private lateinit var bindingResult: BindingResult

    @MockK
    private lateinit var methodParameter: MethodParameter

    @Test
    fun validationErrorsAreMappedToErrorDTO() {
        val fieldError_1 = FieldError("", USERNAME_FIELD, USERNAME_ERROR_MESSAGE_1)
        val fieldError_2 = FieldError("", PASSWORD_FIELD, PASSWORD_ERROR_MESSAGE_1)
        val fieldError_3 = FieldError("", USERNAME_FIELD, USERNAME_ERROR_MESSAGE_2)
        every { bindingResult.fieldErrors } returns listOf(fieldError_1, fieldError_2, fieldError_3)
        val methodArgumentNotValidException = MethodArgumentNotValidException(methodParameter, bindingResult)

        val errorDTO: ErrorDTO = controllerAdvice.handleValidationError(methodArgumentNotValidException, webRequest)

        assertThat(errorDTO.errorCode).isEqualTo(VALIDATION_ERROR)
        assertThat(errorDTO.message).isEqualTo(VALIDATION_ERROR_MESSAGE)
        assertThat(errorDTO.fieldErrors).hasSize(2)

        val usernameErrors: List<String> = errorDTO.fieldErrors.get(USERNAME_FIELD)!!
        assertThat(usernameErrors).hasSize(2)
        assertThat(usernameErrors[0]).isEqualTo(USERNAME_ERROR_MESSAGE_1)
        assertThat(usernameErrors[1]).isEqualTo(USERNAME_ERROR_MESSAGE_2)

        val passwordErrors: List<String> = errorDTO.fieldErrors.get(PASSWORD_FIELD)!!
        assertThat(passwordErrors).hasSize(1)
        assertThat(passwordErrors[0]).isEqualTo(PASSWORD_ERROR_MESSAGE_1)
    }

    @Test
    fun handleValidationExceptionReturnsErrorDTO() {
        val message = "errorMessage"
        val errorDTO: ErrorDTO = controllerAdvice.handleValidationException(ValidationException(message), webRequest)

        assertThat(errorDTO.errorCode).isEqualTo(VALIDATION_ERROR)
        assertThat(errorDTO.message).isEqualTo(message)
        assertThat(errorDTO.fieldErrors).hasSize(0)
    }

    @Test
    fun handleNotFoundExceptionReturnsErrorDTO() {
        val errorDTO: ErrorDTO = controllerAdvice.handleNotFoundException(NotFoundException(AccountId(1)), webRequest)

        assertThat(errorDTO.errorCode).isEqualTo(NOT_FOUND)
        assertThat(errorDTO.message).isEqualTo(NOT_FOUND_ERROR_MESSAGE)
        assertThat(errorDTO.fieldErrors).hasSize(0)
    }

    @Test
    fun handleAccessDeniedExceptionReturnsErrorDTO() {
        val errorDTO: ErrorDTO =
            controllerAdvice.handleAccessDeniedException(
                AccessDeniedException(
                    PASSWORD_ERROR_MESSAGE_1,
                ),
                webRequest,
            )

        assertThat(errorDTO.errorCode).isEqualTo(ACCESS_DENIED)
        assertThat(errorDTO.message).isEqualTo(DENIED_ERROR_MESSAGE)
        assertThat(errorDTO.fieldErrors).hasSize(0)
    }

    @Test
    fun handleForbiddenExceptionReturnsErrorDTO() {
        val errorDTO: ErrorDTO = controllerAdvice.handleForbiddenException(ForbiddenException(), webRequest)

        assertThat(errorDTO.errorCode).isEqualTo(ACCESS_DENIED)
        assertThat(errorDTO.message).isEqualTo(DENIED_ERROR_MESSAGE)
        assertThat(errorDTO.fieldErrors).hasSize(0)
    }

    @Test
    fun handleUnexpectedExceptionReturnsErrorDTO() {
        val errorDTO: ErrorDTO = controllerAdvice.handleUnexpectedException(RuntimeException(PASSWORD_ERROR_MESSAGE_1), webRequest)

        assertThat(errorDTO.errorCode).isEqualTo(UNEXPECTED_ERROR)
        assertThat(errorDTO.message).isEqualTo(UNEXPECTED_ERROR_MESSAGE)
        assertThat(errorDTO.fieldErrors).hasSize(0)
    }
}
