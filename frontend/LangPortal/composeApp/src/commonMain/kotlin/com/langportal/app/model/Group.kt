package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: Long,
    val name: String,
    val description: String? = null,
    val wordsCount: Int = 0
)