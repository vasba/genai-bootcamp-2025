package com.langportal.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.langportal.dto.WordDTO
import com.langportal.dto.WordReviewItemDTO
import com.langportal.model.*
import com.langportal.repository.*
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
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudySessionControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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

    private lateinit var vocabularyGroup: Group
    private lateinit var languageActivity: StudyActivity
    private lateinit var romanianWord: Word
    private lateinit var vocabularySession: StudySession

    @BeforeEach
    fun setup() {
        // Clean up existing data
        wordReviewItemRepository.deleteAll()
        studySessionRepository.deleteAll()
        studyActivityRepository.deleteAll()
        groupRepository.deleteAll()
        wordRepository.deleteAll()
        entityManager.flush()

        // Create and save a Romanian word
        romanianWord =
            Word(
                sourceWord = "casa",
                targetWord = "house",
            ).let {
                wordRepository.save(it)
            }

        // Create and save the vocabulary group
        vocabularyGroup =
            Group(
                name = "Romanian Basic Vocabulary",
                wordsCount = 1,
                description = "Essential Romanian words for beginners",
                words = mutableListOf(romanianWord),
            ).let {
                groupRepository.save(it)
            }

        // Create and save language learning activity
        languageActivity =
            StudyActivity(
                name = "Vocabulary Practice",
                url = "vocab-practice",
            ).let {
                studyActivityRepository.save(it)
            }

        // Create and save study session
        vocabularySession =
            StudySession(
                group = vocabularyGroup,
                studyActivity = languageActivity,
                startTime = LocalDateTime.now(),
                reviewItems = mutableListOf(),
            ).let {
                studySessionRepository.save(it)
            }

        entityManager.flush()
    }

    @Test
    fun `createSession creates new study session successfully`() {
        val request =
            mapOf(
                "groupId" to vocabularyGroup.id,
                "studyActivityId" to languageActivity.id,
            )

        mockMvc
            .perform(
                post("/study-sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.groupId").value(vocabularyGroup.id))
            .andExpect(jsonPath("$.data.studyActivityId").value(languageActivity.id))
    }

    @Test
    fun `addReview adds word review successfully`() {
        val review =
            WordReviewItemDTO(
                word =
                    WordDTO(
                        id = romanianWord.id!!,
                        sourceWord = romanianWord.sourceWord,
                        targetWord = romanianWord.targetWord,
                    ),
                id = 50L,
                correct = true,
                timestamp = LocalDateTime.now(),
            )

        mockMvc
            .perform(
                post("/study-sessions/${vocabularySession.id}/review")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(review)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data").value(true))
    }

    @Test
    fun `getSessionReviews returns reviews for session`() {
        // First add a review
        val review =
            WordReviewItem(
                studySession = vocabularySession,
                word = romanianWord,
                correct = true,
                timestamp = LocalDateTime.now(),
            )
        wordReviewItemRepository.save(review)
        entityManager.flush()

        mockMvc
            .perform(
                get("/study-sessions/${vocabularySession.id}/reviews")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data[0].word.id").value(romanianWord.id))
            .andExpect(jsonPath("$.data[0].correct").value(true))
    }

    @Test
    fun `getSessionsByActivity returns paginated sessions`() {
        mockMvc
            .perform(
                get("/study-sessions")
                    .param("activityId", languageActivity.id.toString())
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].id").value(vocabularySession.id))
    }

    @Test
    fun `getSessionById returns correct session`() {
        mockMvc
            .perform(
                get("/study-sessions/${vocabularySession.id}")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(vocabularySession.id))
            .andExpect(jsonPath("$.groupId").value(vocabularyGroup.id))
            .andExpect(jsonPath("$.studyActivityId").value(languageActivity.id))
    }

    @Test
    fun `updateSession updates session end time successfully`() {
        val endTime = LocalDateTime.now()
        val updateRequest =
            StudySession(
                id = vocabularySession.id,
                group = vocabularyGroup,
                studyActivity = languageActivity,
                startTime = vocabularySession.startTime,
                endTime = endTime,
            )

        mockMvc
            .perform(
                put("/study-sessions/${vocabularySession.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(vocabularySession.id))
    }

    @Test
    fun `getSessionReviewItems returns review items for session`() {
        // Add a review item first
        val reviewItem =
            WordReviewItem(
                studySession = vocabularySession,
                word = romanianWord,
                correct = true,
                timestamp = LocalDateTime.now(),
            )
        wordReviewItemRepository.save(reviewItem)

        // Update the session's review items list
        vocabularySession.reviewItems.add(reviewItem)
        studySessionRepository.save(vocabularySession)

        entityManager.flush()
        entityManager.clear() // Clear the persistence context to ensure we're reading from DB

        val result =
            mockMvc
                .perform(
                    get("/study-sessions/${vocabularySession.id}/review-items")
                        .contentType(MediaType.APPLICATION_JSON),
                )
        result
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.[0].word.id").value(romanianWord.id))
            .andExpect(jsonPath("$.[0].correct").value(true))
    }

    @Test
    fun `getLastStudySession returns most recent session`() {
        mockMvc
            .perform(
                get("/study-sessions/last")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(vocabularySession.id))
    }

    @Test
    fun `createSession returns 404 for non-existent group`() {
        val request =
            mapOf(
                "groupId" to 999L,
                "studyActivityId" to languageActivity.id,
            )

        mockMvc
            .perform(
                post("/study-sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `getSessionById returns 404 for non-existent session`() {
        mockMvc
            .perform(
                get("/study-sessions/999")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isNotFound)
    }
}
