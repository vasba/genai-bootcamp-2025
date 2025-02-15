package com.langportal.controller

import com.langportal.model.StudyActivity
import com.langportal.service.StudyActivityService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class StudyActivityControllerTest {
    private lateinit var studyActivityService: StudyActivityService
    private lateinit var studyActivityController: StudyActivityController

    @BeforeEach
    fun setup() {
        studyActivityService = mockk(relaxed = true)
        studyActivityController = StudyActivityController(studyActivityService)
    }

    @Test
    fun `getAllActivities returns list of activities`() {
        // given
        val activities = listOf(
            StudyActivity(id = 1, name = "Activity 1", url = "url1"),
            StudyActivity(id = 2, name = "Activity 2", url = "url2")
        )
        every { studyActivityService.getAllActivities() } returns activities

        // when
        val response = studyActivityController.getAllActivities()

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body).hasSize(2)
        verify { studyActivityService.getAllActivities() }
    }

    @Test
    fun `getActivityById returns activity when found`() {
        // given
        val activityId = 1L
        val activity = StudyActivity(id = activityId, name = "Test Activity", url = "test-url")
        every { studyActivityService.getActivityById(activityId) } returns activity

        // when
        val response = studyActivityController.getActivityById(activityId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.id).isEqualTo(activityId)
        verify { studyActivityService.getActivityById(activityId) }
    }

    @Test
    fun `deleteActivity returns success`() {
        // given
        val activityId = 1L
        every { studyActivityService.deleteActivity(activityId) } returns Unit

        // when
        val response = studyActivityController.deleteActivity(activityId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        verify { studyActivityService.deleteActivity(activityId) }
    }
}