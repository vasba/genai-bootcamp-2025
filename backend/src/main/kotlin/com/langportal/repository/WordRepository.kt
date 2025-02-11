package com.langportal.repository

import com.langportal.model.Word
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : JpaRepository<Word, Long> {
    @Query("SELECT w FROM Word w WHERE w.sourceWord LIKE %:query% OR w.targetWord LIKE %:query%")
    fun searchWords(query: String): List<Word>

    @Query("SELECT w FROM Word w WHERE w.id IN :ids")
    fun findWordsByIds(ids: List<Long>): List<Word>

    @Query("SELECT w FROM Word w WHERE w.sourceWord LIKE %:query% OR w.targetWord LIKE %:query%")
    fun searchByQuery(
        query: String,
        pageable: Pageable,
    ): Page<Word>
}
