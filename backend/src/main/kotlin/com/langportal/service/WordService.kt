package com.langportal.service

import com.langportal.model.Word
import com.langportal.repository.WordRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordService(private val wordRepository: WordRepository) {

    @Transactional(readOnly = true)
    fun getWords(query: String, pageable: Pageable): Page<Word> {
        return wordRepository.searchByQuery(query, pageable)
    }

    @Transactional(readOnly = true)
    fun getWordById(id: Long): Word {
        return wordRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Word not found with id: $id") }
    }

    @Transactional
    fun createWord(word: Word): Word {
        return wordRepository.save(word)
    }

    @Transactional(readOnly = true)
    fun findWordsByIds(ids: List<Long>): List<Word> {
        return wordRepository.findWordsByIds(ids)
    }
}