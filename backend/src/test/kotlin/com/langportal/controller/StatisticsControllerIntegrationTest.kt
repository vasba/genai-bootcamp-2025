package com.langportal.controller

import com.langportal.model.*
import com.langportal.repository.*
import com.langportal.service.StatisticsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import jakarta.persistence.EntityManager

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StatisticsControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var wordRepository: WordRepository

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var studySessionRepository: StudySessionRepository

    @Autowired
    private lateinit var wordReviewItemRepository: WordReviewItemRepository

    @Autowired
    private lateinit var studyActivityRepository: StudyActivityRepository
    
    @Autowired
    private lateinit var entityManager: EntityManager

    private lateinit var testGroup: Group
    private lateinit var testSession: StudySession
    private lateinit var testWord: Word

    @BeforeEach
    fun setup() {
        // Clean up existing data
        wordReviewItemRepository.deleteAll()
        studySessionRepository.deleteAll()
        studyActivityRepository.deleteAll()
        groupRepository.deleteAll()
        wordRepository.deleteAll()
        entityManager.flush()
        
        // Create and save word first
        testWord = Word(
            sourceWord = "test",
            targetWord = "test"
        ).let {
            wordRepository.save(it)
        }

        // Create and save the group
        testGroup = Group(
            name = "Test Group",
            wordsCount = 0,
            description = "Test Description",
            words = mutableListOf(testWord)
        ).let { 
            groupRepository.save(it)
        }
        
        // Create and save activity
        val activity = StudyActivity(
            name = "Test Activity",
            url = "test-url"
        ).let {
            studyActivityRepository.save(it)
        }
        
        // Create and save study session
        testSession = StudySession(
            group = testGroup,
            studyActivity = activity,
            startTime = LocalDateTime.now()
        ).let {
            studySessionRepository.save(it)
        }
        
        // Create and save word reviews
        val reviews = listOf(
            WordReviewItem(
                studySession = testSession,
                word = testWord,
                correct = true,
                timestamp = LocalDateTime.now()
            ),
            WordReviewItem(
                studySession = testSession,
                word = testWord,
                correct = false,
                timestamp = LocalDateTime.now()
            ),
            WordReviewItem(
                studySession = testSession,
                word = testWord,
                correct = true,
                timestamp = LocalDateTime.now()
            )
        )
        wordReviewItemRepository.saveAll(reviews)
        entityManager.flush()
    }

    @Test
    fun `getSessionStatistics returns correct statistics for existing session`() {
        mockMvc.perform(get("/statistics/sessions/${testSession.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalReviews").value(3))
            .andExpect(jsonPath("$.correctReviews").value(2))
            .andExpect(jsonPath("$.accuracy").value(2.0/3.0))
    }

    @Test
    fun `getSessionStatistics returns 404 for non-existent session`() {
        mockMvc.perform(get("/statistics/sessions/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getGroupStatistics returns correct statistics for existing group`() {
        mockMvc.perform(get("/statistics/groups/${testGroup.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalReviews").value(3))
            .andExpect(jsonPath("$.correctReviews").value(2))
            .andExpect(jsonPath("$.accuracy").value(2.0/3.0))
    }

    @Test
    fun `getGroupStatistics returns 404 for non-existent group`() {
        mockMvc.perform(get("/statistics/groups/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getWordStatistics returns correct overall statistics`() {
        mockMvc.perform(get("/statistics/words")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalReviews").value(3))
            .andExpect(jsonPath("$.correctReviews").value(2))
            .andExpect(jsonPath("$.accuracy").value(2.0/3.0))
    }
}