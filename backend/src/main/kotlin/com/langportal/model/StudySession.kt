package com.langportal.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "study_sessions")
data class StudySession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val studyActivity: StudyActivity,

    @Column(nullable = false)
    val startTime: LocalDateTime,

    @Column
    val endTime: LocalDateTime? = null,

    @OneToMany(mappedBy = "studySession", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val reviewItems: MutableList<WordReviewItem> = mutableListOf()
)