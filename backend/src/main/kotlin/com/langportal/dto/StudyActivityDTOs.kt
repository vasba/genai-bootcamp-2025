package com.langportal.dto

import java.time.LocalDateTime

data class StudyActivityDTO(
    val id: Long,
    val title: String,
    val launchUrl: String,
    val previewUrl: String? = null
)

data class StudySessionDTO(
    val id: Long,
    val groupId: Long,
    val groupName: String,
    val activityId: Long,
    val activityName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val reviewItemsCount: Int,
    val studyActivityId: Long,
    val createdAt: LocalDateTime
)

data class StudyActivityLaunchDataDTO(
    val activity: StudyActivityDTO,
    val groups: List<GroupDTO>
)

data class PagedResponse<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val perPage: Int,
    val totalPages: Int
)