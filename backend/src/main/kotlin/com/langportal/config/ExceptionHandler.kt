package com.langportal.config

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import jakarta.persistence.EntityNotFoundException

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEntityNotFound(ex: EntityNotFoundException): Map<String, String> {
        return mapOf("error" to (ex.message ?: "Entity not found"))
    }
}