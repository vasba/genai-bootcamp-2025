package com.langportal.service

import com.langportal.model.WordReviewItem
import com.langportal.repository.StudySessionRepository
import com.langportal.repository.WordRepository
import com.langportal.repository.WordReviewItemRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordReviewService(
    private val wordReviewItemRepository: WordReviewItemRepository,
    private val wordRepository: WordRepository,
    private val studySessionRepository: StudySessionRepository,
) {
    @Transactional
    fun createWordReview(
        sessionId: Long,
        wordId: Long,
        correct: Boolean,
    ): WordReviewItem {
        val word =
            wordRepository
                .findById(wordId)
                .orElseThrow { EntityNotFoundException("Word not found with id: $wordId") }
        val studySession =
            studySessionRepository
                .findById(sessionId)
                .orElseThrow { EntityNotFoundException("Study session not found with id: $sessionId") }

        val review =
            WordReviewItem(
                word = word,
                studySession = studySession,
                correct = correct,
            )
        return wordReviewItemRepository.save(review)
    }

    @Transactional(readOnly = true)
    fun getSessionReviews(sessionId: Long): List<WordReviewItem> = wordReviewItemRepository.findByStudySessionId(sessionId)
}
