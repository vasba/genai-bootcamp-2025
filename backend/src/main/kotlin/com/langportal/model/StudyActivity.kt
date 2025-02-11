package com.langportal.model

import jakarta.persistence.*

@Entity
@Table(name = "study_activities")
data class StudyActivity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val url: String,

    @OneToMany(mappedBy = "studyActivity", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val sessions: MutableList<StudySession> = mutableListOf()
)