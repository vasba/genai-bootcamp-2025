package com.langportal.controller

import com.langportal.service.ReviewStatistics
import com.langportal.service.StatisticsService
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.NoSuchElementException

class StatisticsControllerTest {
    private val statisticsService = mockk<StatisticsService>(relaxed = true)
    private lateinit var statisticsController: StatisticsController

    @BeforeEach
    fun setup() {
        clearMocks(statisticsService)
        statisticsController = StatisticsController(statisticsService)
    }

    @Test
    fun `getSessionStatistics returns statistics when session exists`() {
        // given
        val sessionId = 1L
        val stats = ReviewStatistics(
            totalReviews = 10,
            correctReviews = 8,
            accuracy = 0.8
        )
        every { statisticsService.getSessionStatistics(sessionId) } returns stats

        // when
        val result = statisticsController.getSessionStatistics(sessionId)

        // then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isNotNull
        assertThat(result.body?.totalReviews).isEqualTo(10)
        assertThat(result.body?.correctReviews).isEqualTo(8)
        assertThat(result.body?.accuracy).isEqualTo(0.8)
        verify { statisticsService.getSessionStatistics(sessionId) }
    }

    @Test
    fun `getGroupStatistics returns statistics when group exists`() {
        // given
        val groupId = 1L
        val stats = ReviewStatistics(
            totalReviews = 100,
            correctReviews = 75,
            accuracy = 0.75
        )
        every { statisticsService.getGroupStatistics(groupId) } returns stats

        // when
        val result = statisticsController.getGroupStatistics(groupId)

        // then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isNotNull
        assertThat(result.body?.totalReviews).isEqualTo(100)
        assertThat(result.body?.correctReviews).isEqualTo(75)
        assertThat(result.body?.accuracy).isEqualTo(0.75)
        verify { statisticsService.getGroupStatistics(groupId) }
    }

    @Test
    fun `getSessionStatistics returns not found when session does not exist`() {
        // given
        val nonExistentSessionId = 999L
        every { statisticsService.getSessionStatistics(nonExistentSessionId) } throws NoSuchElementException("Session not found")

        // when
        val result = statisticsController.getSessionStatistics(nonExistentSessionId)

        // then
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        verify { statisticsService.getSessionStatistics(nonExistentSessionId) }
    }

    @Test
    fun `getGroupStatistics returns not found when group does not exist`() {
        // given
        val nonExistentGroupId = 999L
        every { statisticsService.getGroupStatistics(nonExistentGroupId) } throws NoSuchElementException("Group not found")

        // when
        val result = statisticsController.getGroupStatistics(nonExistentGroupId)

        // then
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        verify { statisticsService.getGroupStatistics(nonExistentGroupId) }
    }
}