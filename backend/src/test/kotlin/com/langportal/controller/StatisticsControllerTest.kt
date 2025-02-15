package com.langportal.controller

import com.langportal.service.ReviewStatistics
import com.langportal.service.StatisticsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class StatisticsControllerTest {
    private lateinit var statisticsService: StatisticsService
    private lateinit var statisticsController: StatisticsController

    @BeforeEach
    fun setup() {
        statisticsService = mockk(relaxed = true)
        statisticsController = StatisticsController(statisticsService)
    }

    @Test
    fun `getSessionStatistics returns statistics`() {
        // given
        val sessionId = 1L
        val stats = ReviewStatistics(
            totalReviews = 10,
            correctReviews = 8,
            accuracy = 0.8
        )
        every { statisticsService.getSessionStatistics(sessionId) } returns stats

        // when
        val response = statisticsController.getSessionStatistics(sessionId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.totalReviews).isEqualTo(10)
        assertThat(response.body?.correctReviews).isEqualTo(8)
        assertThat(response.body?.accuracy).isEqualTo(0.8)
        verify { statisticsService.getSessionStatistics(sessionId) }
    }

    @Test
    fun `getGroupStatistics returns statistics`() {
        // given
        val groupId = 1L
        val stats = ReviewStatistics(
            totalReviews = 20,
            correctReviews = 15,
            accuracy = 0.75
        )
        every { statisticsService.getGroupStatistics(groupId) } returns stats

        // when
        val response = statisticsController.getGroupStatistics(groupId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.totalReviews).isEqualTo(20)
        assertThat(response.body?.correctReviews).isEqualTo(15)
        assertThat(response.body?.accuracy).isEqualTo(0.75)
        verify { statisticsService.getGroupStatistics(groupId) }
    }
}