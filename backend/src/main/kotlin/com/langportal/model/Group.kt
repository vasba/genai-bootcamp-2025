package com.langportal.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column
    var description: String? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "group_words",
        joinColumns = [JoinColumn(name = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "word_id")]
    )
    var words: MutableList<Word> = mutableListOf(),

    @Transient
    var wordsCount: Int = 0
)