package com.langportal.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "word_review_items")
data class WordReviewItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    val word: Word,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_session_id", nullable = false)
    val studySession: StudySession,

    @Column(nullable = false)
    val correct: Boolean,

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)