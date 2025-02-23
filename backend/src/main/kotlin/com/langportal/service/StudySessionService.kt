package com.langportal.service

import com.langportal.model.*
import com.langportal.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityNotFoundException
import java.time.LocalDateTime

@Service
class StudySessionService(
    private val studySessionRepository: StudySessionRepository,
    private val groupService: GroupService,
    private val studyActivityService: StudyActivityService,
    private val wordReviewItemRepository: WordReviewItemRepository,
    private val wordRepository: WordRepository,
    private val studyActivityRepository: StudyActivityRepository,
    private val groupRepository: GroupRepository
) {
    @Transactional(readOnly = true)
    fun getStudySessionsByActivity(activityId: Long, pageable: Pageable): Page<StudySession> {
        if (!studyActivityRepository.existsById(activityId)) {
            throw EntityNotFoundException("Study activity not found with id: $activityId")
        }
        return studySessionRepository.findByStudyActivityId(activityId, pageable)
    }

    @Transactional
    fun createStudySession(studyActivity: Long, group: Long): StudySession {
        val activity = studyActivityRepository.findById(studyActivity)
            .orElseThrow { EntityNotFoundException("Study activity not found with id: $studyActivity") }
        val groupRef = groupService.getGroup(group)
            
        val session = StudySession(
            studyActivity = activity,
            group = groupRef,
            startTime = LocalDateTime.now()
        )
        return studySessionRepository.save(session)
    }

    @Transactional(readOnly = true)
    fun getStudySessionById(id: Long): StudySession {
        return studySessionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Study session not found with id: $id") }
    }

    @Transactional
    fun addReview(sessionId: Long, wordId: Long, correct: Boolean): WordReviewItem? {
        val session = studySessionRepository.findById(sessionId).orElse(null) ?: return null
        val word = wordRepository.findById(wordId).orElse(null) ?: return null
        
        val review = WordReviewItem(
            word = word,
            studySession = session,
            correct = correct
        )
        
        return wordReviewItemRepository.save(review)
    }

    @Transactional
    fun updateStudySession(id: Long, endTime: LocalDateTime): StudySession {
        val existingSession = studySessionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Study session not found with id: $id") }
            
        val updatedSession = existingSession.copy(
            endTime = endTime
        )
        return studySessionRepository.save(updatedSession)
    }

    @Transactional(readOnly = true)
    fun getLastStudySession(): StudySession? {
        return studySessionRepository.findTopByOrderByStartTimeDesc()
    }
}