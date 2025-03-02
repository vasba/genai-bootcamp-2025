package com.langportal.controller

import com.langportal.dto.*
import com.langportal.mapper.ModelMapper
import com.langportal.model.*
import com.langportal.service.StudySessionService
import com.langportal.service.WordReviewService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class StudySessionControllerTest {
    private lateinit var studySessionService: StudySessionService
    private lateinit var wordReviewService: WordReviewService
    private lateinit var modelMapper: ModelMapper
    private lateinit var studySessionController: StudySessionController

    @BeforeEach
    fun setup() {
        studySessionService = mockk(relaxed = true)
        wordReviewService = mockk(relaxed = true)
        modelMapper = mockk(relaxed = true)
        studySessionController = StudySessionController(studySessionService, wordReviewService, modelMapper)
    }

    @Test
    fun `createSession returns new session`() {
        // given
        val request = StudySessionController.CreateSessionRequest(groupId = 1L, studyActivityId = 1L)
        val group = Group(id = 1L, name = "Test Group")
        val activity = StudyActivity(id = 1L, name = "Test Activity", url = "test-url")
        val session =
            StudySession(
                id = 1L,
                group = group,
                studyActivity = activity,
                startTime = LocalDateTime.now(),
            )
        val sessionDTO =
            StudySessionDTO(
                id = 1L,
                groupId = 1L,
                groupName = "Test Group",
                activityId = 1L,
                activityName = "Test Activity",
                startTime = LocalDateTime.now(),
                endTime = null,
                reviewItemsCount = 0,
                studyActivityId = 1L,
                createdAt = LocalDateTime.now(),
            )

        every { studySessionService.createStudySession(1L, 1L) } returns session
        every { modelMapper.toStudySessionDTO(session) } returns sessionDTO

        // when
        val response = studySessionController.createSession(request)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.data?.id).isEqualTo(1L)
        verify { studySessionService.createStudySession(1L, 1L) }
    }

    @Test
    fun `addReview creates new review`() {
        // given
        val sessionId = 1L
        val wordId = 1L
        val reviewRequest = WordReviewRequestDTO(
            wordId = wordId,
            correct = true
        )

        val word = Word(id = wordId, sourceWord = "test", targetWord = "test")
        val session =
            StudySession(
                id = sessionId,
                group = Group(id = 1L, name = "Test Group"),
                studyActivity = StudyActivity(id = 1L, name = "Test Activity", url = "test-url"),
                startTime = LocalDateTime.now(),
            )

        val savedReview =
            WordReviewItem(
                id = 1L,
                word = word,
                studySession = session,
                correct = true,
                timestamp = LocalDateTime.now(),
            )

        every { wordReviewService.createWordReview(sessionId, wordId, true) } returns savedReview

        // when
        val response = studySessionController.addReview(sessionId, reviewRequest)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.data).isTrue()
        verify { wordReviewService.createWordReview(sessionId, wordId, true) }
    }

    @Test
    fun `getSessionReviews returns list of reviews`() {
        // given
        val sessionId = 1L
        val group = Group(id = 1L, name = "Test Group")
        val activity = StudyActivity(id = 1L, name = "Test Activity", url = "test-url")
        val session =
            StudySession(
                id = sessionId,
                group = group,
                studyActivity = activity,
                startTime = LocalDateTime.now(),
            )
        val word = Word(id = 1L, sourceWord = "test", targetWord = "test")
        val wordReviewItem =
            WordReviewItem(
                id = 1L,
                studySession = session,
                word = word,
                correct = true,
                timestamp = LocalDateTime.now(),
            )
        val reviewItemDTO =
            WordReviewItemDTO(
                id = 1L,
                word = WordDTO(id = 1L, sourceWord = "test", targetWord = "test"),
                correct = true,
                timestamp = LocalDateTime.now(),
            )
        every { wordReviewService.getSessionReviews(sessionId) } returns listOf(wordReviewItem)
        every { modelMapper.toWordReviewItemDTO(wordReviewItem) } returns reviewItemDTO

        // when
        val response = studySessionController.getSessionReviews(sessionId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.data).hasSize(1)
        verify { wordReviewService.getSessionReviews(sessionId) }
    }

    @Test
    fun `getSessionsByActivity returns paginated sessions`() {
        // given
        val activityId = 1L
        val pageable = PageRequest.of(0, 10)
        val group = Group(id = 1L, name = "Test Group")
        val activity = StudyActivity(id = 1L, name = "Test Activity", url = "test-url")
        val session =
            StudySession(
                id = 1L,
                group = group,
                studyActivity = activity,
                startTime = LocalDateTime.now(),
            )
        val sessionDTO =
            StudySessionDTO(
                id = 1L,
                groupId = 1L,
                groupName = "Test Group",
                activityId = 1L,
                activityName = "Test Activity",
                startTime = LocalDateTime.now(),
                endTime = null,
                reviewItemsCount = 0,
                studyActivityId = 1L,
                createdAt = LocalDateTime.now(),
            )
        val page = PageImpl(listOf(session))
        every { studySessionService.getStudySessionsByActivity(activityId, pageable) } returns page
        every { modelMapper.toStudySessionDTO(session) } returns sessionDTO

        // when
        val response = studySessionController.getSessionsByActivity(activityId, pageable)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.content).hasSize(1)
        verify { studySessionService.getStudySessionsByActivity(activityId, pageable) }
    }

    @Test
    fun `getSessionById returns session when found`() {
        // given
        val sessionId = 1L
        val group = Group(id = 1L, name = "Test Group")
        val activity = StudyActivity(id = 1L, name = "Test Activity", url = "test-url")
        val session =
            StudySession(
                id = sessionId,
                group = group,
                studyActivity = activity,
                startTime = LocalDateTime.now(),
            )
        val sessionDTO =
            StudySessionDTO(
                id = sessionId,
                groupId = 1L,
                groupName = "Test Group",
                activityId = 1L,
                activityName = "Test Activity",
                startTime = LocalDateTime.now(),
                endTime = null,
                reviewItemsCount = 0,
                studyActivityId = 1L,
                createdAt = LocalDateTime.now(),
            )
        every { studySessionService.getStudySessionById(sessionId) } returns session
        every { modelMapper.toStudySessionDTO(session) } returns sessionDTO

        // when
        val response = studySessionController.getSessionById(sessionId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.id).isEqualTo(sessionId)
        verify { studySessionService.getStudySessionById(sessionId) }
    }

    @Test
    fun `updateSession returns updated session`() {
        // given
        val sessionId = 1L
        val endTime = LocalDateTime.now()
        val group = Group(id = 1L, name = "Test Group")
        val activity = StudyActivity(id = 1L, name = "Test Activity", url = "test-url")
        val session =
            StudySession(
                id = sessionId,
                group = group,
                studyActivity = activity,
                startTime = LocalDateTime.now(),
                endTime = endTime,
            )
        val updatedSession = session.copy()
        val sessionDTO =
            StudySessionDTO(
                id = sessionId,
                groupId = 1L,
                groupName = "Test Group",
                activityId = 1L,
                activityName = "Test Activity",
                startTime = LocalDateTime.now(),
                endTime = endTime,
                reviewItemsCount = 0,
                studyActivityId = 1L,
                createdAt = LocalDateTime.now(),
            )
        every { studySessionService.updateStudySession(sessionId, endTime) } returns updatedSession
        every { modelMapper.toStudySessionDTO(updatedSession) } returns sessionDTO

        // when
        val response = studySessionController.updateSession(sessionId, session)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.id).isEqualTo(sessionId)
        verify { studySessionService.updateStudySession(sessionId, endTime) }
    }

    @Test
    fun `getSessionReviewItems returns list of review items`() {
        // given
        val sessionId = 1L
        val session = mockk<StudySession>(relaxed = true)
        val reviewItem = mockk<WordReviewItem>(relaxed = true)
        val reviewItemDTO =
            WordReviewItemDTO(
                id = 1L,
                word = WordDTO(id = 1L, sourceWord = "test", targetWord = "test"),
                correct = true,
                timestamp = LocalDateTime.now(),
            )
        every { session.reviewItems } returns mutableListOf(reviewItem)
        every { studySessionService.getStudySessionById(sessionId) } returns session
        every { modelMapper.toWordReviewItemDTO(reviewItem) } returns reviewItemDTO

        // when
        val response = studySessionController.getSessionReviewItems(sessionId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body).hasSize(1)
        verify { studySessionService.getStudySessionById(sessionId) }
        verify { modelMapper.toWordReviewItemDTO(reviewItem) }
    }

    @Test
    fun `getLastStudySession returns the latest session`() {
        // given
        val group = Group(id = 1L, name = "Test Group")
        val activity = StudyActivity(id = 1L, name = "Test Activity", url = "test-url")
        val session1 =
            StudySession(
                id = 1L,
                group = group,
                studyActivity = activity,
                startTime = LocalDateTime.now().minusDays(1),
            )
        val session2 =
            StudySession(
                id = 2L,
                group = group,
                studyActivity = activity,
                startTime = LocalDateTime.now(),
            )
        val sessionDTO =
            StudySessionDTO(
                id = 2L,
                groupId = 1L,
                groupName = "Test Group",
                activityId = 1L,
                activityName = "Test Activity",
                startTime = session2.startTime,
                endTime = null,
                reviewItemsCount = 0,
                studyActivityId = 1L,
                createdAt = LocalDateTime.now(),
            )
        every { studySessionService.getLastStudySession() } returns session2
        every { modelMapper.toStudySessionDTO(session2) } returns sessionDTO
        // when
        val response = studySessionController.getLastStudySession()
        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.id).isEqualTo(2L)
        verify { studySessionService.getLastStudySession() }
    }
}
