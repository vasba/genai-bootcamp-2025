package com.langportal.model

import jakarta.persistence.*

@Entity
@Table(name = "words")
data class Word(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val sourceWord: String,
    @Column(nullable = false)
    val targetWord: String,
    @ManyToMany(mappedBy = "words", fetch = FetchType.LAZY)
    val groups: MutableList<Group> = mutableListOf(),
)
