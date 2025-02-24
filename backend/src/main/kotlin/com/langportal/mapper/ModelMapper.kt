package com.langportal.mapper

import com.langportal.dto.*
import com.langportal.model.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ModelMapper {
    fun toWordDTO(word: Word): WordDTO {
        return WordDTO(
            id = word.id!!,
            sourceWord = word.sourceWord,
            targetWord = word.targetWord,
            groups = word.groups?.map { toGroupWithoutWords(it) } ?: emptyList()
        )
    }

    fun toGroupDTO(group: Group): GroupDTO {
        return GroupDTO(
            id = group.id!!, // Ensure id is non-nullable
            name = group.name,
            description = group.description,
            words = group.words.map { toWordDTO(it) }
        )
    }

    fun toGroupWithoutWords(group: Group): GroupDTO {
        return GroupDTO(
            id = group.id!!, // Ensure id is non-nullable
            name = group.name,
            description = group.description
        )
    }

    fun toStudySessionDTO(session: StudySession): StudySessionDTO {
        return StudySessionDTO(
            id = session.id!!,
            groupId = session.group.id!!,
            groupName = session.group.name,
            activityId = session.studyActivity.id!!,
            activityName = session.studyActivity.name,
            startTime = session.startTime,
            endTime = session.endTime,
            reviewItemsCount = session.reviewItems.size,
            studyActivityId = session.studyActivity.id!!,
            createdAt = LocalDateTime.now()
        )
    }

    fun toWordReviewItemDTO(item: WordReviewItem): WordReviewItemDTO {
        return WordReviewItemDTO(
            id = item.id!!,
            word = toWordDTO(item.word),
            correct = item.correct,
            timestamp = item.timestamp
        )
    }
}