package com.langportal.controller

import com.langportal.dto.WordDTO
import com.langportal.mapper.ModelMapper
import com.langportal.model.Word
import com.langportal.service.WordService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class WordsResponse(
    val words: List<WordDTO>,
    val totalPages: Int,
    val currentPage: Int,
    val totalWords: Long,
)

@RestController
@RequestMapping("/words")
@CrossOrigin
class WordController(
    private val wordService: WordService,
    private val modelMapper: ModelMapper,
) {
    @GetMapping
    fun getWords(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "romanian") sortBy: String,
        @RequestParam(defaultValue = "asc") order: String,
    ): ResponseEntity<WordsResponse> {
        val pageSize = 50 // Match Flask backend's page size
        val validSortFields = listOf("romanian", "english")
        val actualSortBy = if (sortBy in validSortFields) sortBy else "romanian"
        val actualOrder = if (order.lowercase() in listOf("asc", "desc")) order.lowercase() else "asc"

        val pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(actualOrder), actualSortBy))
        val wordsPage = wordService.getWords("", pageable)

        return ResponseEntity.ok(
            WordsResponse(
                words = wordsPage.content.map { modelMapper.toWordDTO(it) },
                totalPages = wordsPage.totalPages,
                currentPage = page,
                totalWords = wordsPage.totalElements,
            ),
        )
    }

    @GetMapping("/{id}")
    fun getWordById(
        @PathVariable id: Long,
    ): ResponseEntity<WordDTO> {
        val word = wordService.getWordById(id)
        return ResponseEntity.ok(modelMapper.toWordDTO(word))
    }

    @GetMapping("/search")
    fun searchWords(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "sourceWord") sortBy: String,
        @RequestParam(defaultValue = "ASC") sortDir: String,
    ): ResponseEntity<Page<WordDTO>> {
        val pageable = PageRequest.of(page, size, Sort.Direction.valueOf(sortDir), sortBy)
        val words = wordService.getWords(query, pageable)
        return ResponseEntity.ok(words.map { modelMapper.toWordDTO(it) })
    }

    @PostMapping
    fun createWord(
        @RequestBody word: Word,
    ): ResponseEntity<WordDTO> {
        val createdWord = wordService.createWord(word)
        return ResponseEntity.ok(modelMapper.toWordDTO(createdWord))
    }
}
