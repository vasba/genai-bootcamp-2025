package com.langportal.controller

import com.langportal.dto.WordDTO
import com.langportal.mapper.ModelMapper
import com.langportal.model.Word
import com.langportal.service.WordService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.ResponseEntity

class WordControllerTest {
    private val wordService = mockk<WordService>(relaxed = true)
    private val modelMapper = mockk<ModelMapper>(relaxed = true)
    private lateinit var wordController: WordController

    @BeforeEach
    fun setup() {
        clearMocks(wordService, modelMapper)
        wordController = WordController(wordService, modelMapper)
    }

    @Test
    fun `getWords returns paginated list of words`() {
        // Arrange
        val page = 1
        val pageSize = 50
        val word = Word(id = 1, sourceWord = "test", targetWord = "test")
        val wordDTO = WordDTO(id = 1, sourceWord = "test", targetWord = "test")
        val pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "sourceWord"))
        val pageImpl = PageImpl(listOf(word), pageable, 1)

        every { wordService.getWords("", pageable) } returns pageImpl
        every { modelMapper.toWordDTO(word) } returns wordDTO

        // Act
        val result = wordController.getWords(page, "sourceWord", "asc")

        // Assert
        assertThat(result.statusCode.is2xxSuccessful).isTrue()
        assertThat(result.body).isNotNull
        assertThat(result.body?.words).hasSize(1)
        assertThat(result.body?.currentPage).isEqualTo(page)
        verify { wordService.getWords("", pageable) }
    }

    @Test
    fun `getWords returns paginated list when sorting by romanian field`() {
        // Arrange
        val page = 1
        val pageSize = 50
        val word = Word(id = 1, sourceWord = "test", targetWord = "test")
        val wordDTO = WordDTO(id = 1, sourceWord = "test", targetWord = "test")
        val pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "sourceWord"))
        val pageImpl = PageImpl(listOf(word), pageable, 1)

        every { wordService.getWords("", pageable) } returns pageImpl
        every { modelMapper.toWordDTO(word) } returns wordDTO

        // Act
        val result = wordController.getWords(page, "romanian", "asc")

        // Assert
        assertThat(result.statusCode.is2xxSuccessful).isTrue()
        assertThat(result.body).isNotNull
        assertThat(result.body?.words).hasSize(1)
        assertThat(result.body?.currentPage).isEqualTo(page)
        verify { wordService.getWords("", pageable) }
    }

    @Test
    fun `getWords returns paginated list when sorting by english field`() {
        // Arrange
        val page = 1
        val pageSize = 50
        val word = Word(id = 1, sourceWord = "test", targetWord = "test")
        val wordDTO = WordDTO(id = 1, sourceWord = "test", targetWord = "test")
        val pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "targetWord"))
        val pageImpl = PageImpl(listOf(word), pageable, 1)

        every { wordService.getWords("", pageable) } returns pageImpl
        every { modelMapper.toWordDTO(word) } returns wordDTO

        // Act
        val result = wordController.getWords(page, "english", "desc")

        // Assert
        assertThat(result.statusCode.is2xxSuccessful).isTrue()
        assertThat(result.body).isNotNull
        assertThat(result.body?.words).hasSize(1)
        assertThat(result.body?.currentPage).isEqualTo(page)
        verify { wordService.getWords("", pageable) }
    }

    @Test
    fun `getWordById returns word when exists`() {
        // Arrange
        val id = 1L
        val word = Word(id = id, sourceWord = "test", targetWord = "test")
        val wordDTO = WordDTO(id = id, sourceWord = "test", targetWord = "test")

        every { wordService.getWordById(id) } returns word
        every { modelMapper.toWordDTO(word) } returns wordDTO

        // Act
        val result = wordController.getWordById(id)

        // Assert
        assertThat(result.statusCode.is2xxSuccessful).isTrue()
        assertThat(result.body).isNotNull
        assertThat(result.body?.id).isEqualTo(id)
        verify { wordService.getWordById(id) }
    }

    @Test
    fun `searchWords returns filtered paginated list`() {
        // Arrange
        val query = "test"
        val page = 0
        val size = 20
        val word = Word(id = 1, sourceWord = "test", targetWord = "test")
        val wordDTO = WordDTO(id = 1, sourceWord = "test", targetWord = "test")
        val pageable = PageRequest.of(page, size, Sort.Direction.ASC, "sourceWord")
        val pageImpl = PageImpl(listOf(word), pageable, 1)

        every { wordService.getWords(query, pageable) } returns pageImpl
        every { modelMapper.toWordDTO(word) } returns wordDTO

        // Act
        val result = wordController.searchWords(query, page, size, "sourceWord", "ASC")

        // Assert
        assertThat(result.statusCode.is2xxSuccessful).isTrue()
        assertThat(result.body).isNotNull
        assertThat(result.body?.content).hasSize(1)
        verify { wordService.getWords(query, pageable) }
    }

    @Test
    fun `createWord returns created word`() {
        // Arrange
        val word = Word(sourceWord = "test", targetWord = "test")
        val createdWord = Word(id = 1, sourceWord = "test", targetWord = "test")
        val wordDTO = WordDTO(id = 1, sourceWord = "test", targetWord = "test")

        every { wordService.createWord(word) } returns createdWord
        every { modelMapper.toWordDTO(createdWord) } returns wordDTO

        // Act
        val result = wordController.createWord(word)

        // Assert
        assertThat(result.statusCode.is2xxSuccessful).isTrue()
        assertThat(result.body).isNotNull
        assertThat(result.body?.id).isEqualTo(1)
        verify { wordService.createWord(word) }
    }
}