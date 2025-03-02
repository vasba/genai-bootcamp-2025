package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class WordDTO(
    val id: Long,
    val sourceWord: String,
    val targetWord: String
)