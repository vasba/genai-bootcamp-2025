package com.langportal.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import jakarta.persistence.EntityNotFoundException

data class ApiResponse(
    val status: HttpStatus,
    val message: String
)

@RestControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEntityNotFound(ex: EntityNotFoundException): ApiResponse {
        return ApiResponse(HttpStatus.NOT_FOUND, ex.message ?: "Entity not found")
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ApiResponse {
        return ApiResponse(HttpStatus.CONFLICT, ex.message ?: "Data integrity violation")
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid parameter value for ${ex.name}: ${ex.message}"
        )
        return ResponseEntity(apiResponse, HttpStatus.BAD_REQUEST)
    }

    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.message ?: "An unexpected error occurred"
        )
        return ResponseEntity(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}