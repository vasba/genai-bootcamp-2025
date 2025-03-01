package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class StudyActivity(
    val id: Long? = null,
    val name: String,
    val url: String
)