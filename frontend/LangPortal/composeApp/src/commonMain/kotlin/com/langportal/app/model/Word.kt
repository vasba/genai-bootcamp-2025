package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupDTO(
    val id: Long,
    val name: String,
    val description: String? = null
)

@Serializable
data class WordDTO(
    val id: Long,
    val sourceWord: String,
    val targetWord: String,
    val groups: List<GroupDTO>? = null
)