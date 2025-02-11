package com.langportal.repository

import com.langportal.model.WordReviewItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WordReviewItemRepository : JpaRepository<WordReviewItem, Long> {
    @Query("SELECT DISTINCT wri FROM WordReviewItem wri JOIN wri.word w JOIN w.groups g WHERE g.id = :groupId")
    fun findByWordGroupId(groupId: Long): List<WordReviewItem>

    @Query("SELECT wri FROM WordReviewItem wri WHERE wri.studySession.id = :sessionId")
    fun findByStudySessionId(sessionId: Long): List<WordReviewItem>
}
