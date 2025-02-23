package com.langportal.repository

import com.langportal.model.Group
import com.langportal.model.StudyActivity
import com.langportal.model.StudySession
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StudySessionRepository : JpaRepository<StudySession, Long> {
    @Query(
        """
        SELECT s FROM StudySession s 
        LEFT JOIN FETCH s.group 
        LEFT JOIN FETCH s.studyActivity 
        LEFT JOIN FETCH s.reviewItems 
        WHERE s.studyActivity.id = ?1
    """,
    )
    fun findByStudyActivityId(
        activityId: Long,
        pageable: Pageable,
    ): Page<StudySession>

    fun findTopByOrderByStartTimeDesc(): StudySession?
}

interface StudyActivityRepository : JpaRepository<StudyActivity, Long>

interface GroupRepository : JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.words WHERE g.id = :id")
    fun findByIdWithWords(id: Long): Group?
}
