package com.example.howtokotlin.controller

import com.example.howtokotlin.controller.model.ErrorCode.*
import com.example.howtokotlin.controller.model.ErrorDTO
import com.example.howtokotlin.exception.ForbiddenException
import com.example.howtokotlin.exception.NotFoundException
import com.example.howtokotlin.exception.ValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.util.function.Consumer

@RestControllerAdvice
class ControllerAdvice {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationError(
        e: MethodArgumentNotValidException,
        webRequest: WebRequest?,
    ): ErrorDTO {
        log.warn("Validation error while handling request: {}", webRequest)
        return ErrorDTO(
            errorCode = VALIDATION_ERROR,
            message = VALIDATION_ERROR_MESSAGE,
            fieldErrors = getFieldErrors(e),
        )
    }

    @ExceptionHandler(value = [ValidationException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(
        e: ValidationException,
        webRequest: WebRequest?,
    ): ErrorDTO {
        log.warn("Validation error while handling request: {}", webRequest)
        return ErrorDTO(
            errorCode = VALIDATION_ERROR,
            message = e.message,
        )
    }

    @ExceptionHandler(value = [NotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(
        e: NotFoundException?,
        webRequest: WebRequest?,
    ): ErrorDTO {
        log.warn("NotFoundException while handling request: {}", webRequest, e)
        return ErrorDTO(
            errorCode = NOT_FOUND,
            message = NOT_FOUND_ERROR_MESSAGE,
        )
    }

    @ExceptionHandler(value = [ForbiddenException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbiddenException(
        e: ForbiddenException?,
        webRequest: WebRequest?,
    ): ErrorDTO {
        log.warn("ForbiddenException while handling request: {}", webRequest, e)
        return ErrorDTO(
            errorCode = ACCESS_DENIED,
            message = DENIED_ERROR_MESSAGE,
        )
    }

    @ExceptionHandler(value = [AccessDeniedException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(
        e: AccessDeniedException?,
        webRequest: WebRequest?,
    ): ErrorDTO {
        log.warn("AccessDeniedException error while handling request: {}", webRequest)
        return ErrorDTO(
            errorCode = ACCESS_DENIED,
            message = DENIED_ERROR_MESSAGE,
        )
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnexpectedException(
        e: Exception?,
        webRequest: WebRequest?,
    ): ErrorDTO {
        log.error("Unexpected Exception while handler request: {}", webRequest, e)
        return ErrorDTO(
            errorCode = UNEXPECTED_ERROR,
            message = UNEXPECTED_ERROR_MESSAGE,
        )
    }

    companion object {
        const val VALIDATION_ERROR_MESSAGE: String = "Validation error"
        const val NOT_FOUND_ERROR_MESSAGE: String = "Resource not found"
        const val DENIED_ERROR_MESSAGE: String = "Resource denied"
        const val UNEXPECTED_ERROR_MESSAGE: String = "An unexpected error occurred. See logs for details"

        private fun getFieldErrors(e: MethodArgumentNotValidException): Map<String, List<String>> {
            val fieldErrors: MutableMap<String, MutableList<String>> = HashMap()
            e.bindingResult.fieldErrors.forEach(
                Consumer { fieldError: FieldError ->
                    val errors = fieldErrors.computeIfAbsent(fieldError.field) { _: String -> ArrayList() }
                    fieldError.defaultMessage?.let { errors.add(it) }
                },
            )
            return fieldErrors
        }
    }
}
