package com.langportal.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.langportal.model.Word
import com.langportal.repository.WordRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WordControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var wordRepository: WordRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    private lateinit var testWord: Word

    @BeforeEach
    fun setup() {
        // Clean up existing data
        wordRepository.deleteAll()
        entityManager.flush()

        // Create and save test word
        testWord = Word(
            sourceWord = "casa",
            targetWord = "house"
        ).let {
            wordRepository.save(it)
        }
        
        entityManager.flush()
    }

    @Test
    fun `getWords returns paginated list of words`() {
        mockMvc.perform(get("/words")
            .param("page", "1")
            .param("sortBy", "romanian")
            .param("order", "asc")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.words[0].id").value(testWord.id))
            .andExpect(jsonPath("$.words[0].sourceWord").value("casa"))
            .andExpect(jsonPath("$.words[0].targetWord").value("house"))
            .andExpect(jsonPath("$.totalPages").exists())
            .andExpect(jsonPath("$.currentPage").value(1))
            .andExpect(jsonPath("$.totalWords").exists())
    }

    @Test
    fun `getWords handles invalid sort parameters`() {
        mockMvc.perform(get("/words")
            .param("page", "1")
            .param("sortBy", "invalid")
            .param("order", "invalid")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.words[0].id").value(testWord.id))
    }

    @Test
    fun `getWordById returns word for existing id`() {
        mockMvc.perform(get("/words/${testWord.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testWord.id))
            .andExpect(jsonPath("$.sourceWord").value("casa"))
            .andExpect(jsonPath("$.targetWord").value("house"))
    }

    @Test
    fun `searchWords returns matching words`() {
        mockMvc.perform(get("/words/search")
            .param("query", "casa")
            .param("page", "0")
            .param("size", "20")
            .param("sortBy", "sourceWord")
            .param("sortDir", "ASC")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].sourceWord").value("casa"))
    }

    @Test
    fun `createWord creates new word successfully`() {
        val newWord = Word(
            sourceWord = "masă",
            targetWord = "table"
        )

        mockMvc.perform(post("/words")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newWord)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.sourceWord").value("masă"))
            .andExpect(jsonPath("$.targetWord").value("table"))
    }
}