package com.langportal.service

import com.langportal.repository.GroupRepository
import com.langportal.repository.WordReviewItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

data class ReviewStatistics(
    val totalReviews: Int,
    val correctReviews: Int,
    val accuracy: Double,
)

@Service
class StatisticsService(
    private val wordReviewItemRepository: WordReviewItemRepository,
    private val groupRepository: GroupRepository
) {
    @Transactional(readOnly = true)
    fun getSessionStatistics(sessionId: Long): ReviewStatistics {
        val allReviews = wordReviewItemRepository.findByStudySessionId(sessionId)
        if (allReviews.isEmpty()) {
            throw NoSuchElementException("Session not found with id: $sessionId")
        }
        val totalReviews = allReviews.size
        val correctReviews = allReviews.count { it.correct }
        val accuracy =
            if (totalReviews > 0) {
                correctReviews.toDouble() / totalReviews
            } else {
                0.0
            }

        return ReviewStatistics(totalReviews, correctReviews, accuracy)
    }

    @Transactional(readOnly = true)
    fun getGroupStatistics(groupId: Long): ReviewStatistics {
        if (!groupRepository.existsById(groupId)) {
            throw NoSuchElementException("Group not found with id: $groupId")
        }
        val allReviews = wordReviewItemRepository.findByWordGroupId(groupId)
        val totalReviews = allReviews.size
        val correctReviews = allReviews.count { it.correct }
        val accuracy =
            if (totalReviews > 0) {
                correctReviews.toDouble() / totalReviews
            } else {
                0.0
            }

        return ReviewStatistics(totalReviews, correctReviews, accuracy)
    }

    @Transactional(readOnly = true)
    fun getWordStatistics(): ReviewStatistics {
        val allReviews = wordReviewItemRepository.findAll()
        val totalReviews = allReviews.size
        val correctReviews = allReviews.count { it.correct }
        val accuracy =
            if (totalReviews > 0) {
                correctReviews.toDouble() / totalReviews
            } else {
                0.0
            }

        return ReviewStatistics(totalReviews, correctReviews, accuracy)
    }
}
