package com.langportal.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.langportal.model.StudyActivity
import com.langportal.repository.StudyActivityRepository
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
class StudyActivityControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var studyActivityRepository: StudyActivityRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    private lateinit var testActivity: StudyActivity

    @BeforeEach
    fun setup() {
        // Clean up existing data
        studyActivityRepository.deleteAll()
        entityManager.flush()

        // Create and save test activity
        testActivity = StudyActivity(
            name = "Test Activity",
            url = "test-url"
        ).let {
            studyActivityRepository.save(it)
        }
        
        entityManager.flush()
    }

    @Test
    fun `getAllActivities returns list of activities`() {
        mockMvc.perform(get("/activities")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(testActivity.id))
            .andExpect(jsonPath("$[0].name").value("Test Activity"))
            .andExpect(jsonPath("$[0].url").value("test-url"))
    }

    @Test
    fun `getActivityById returns activity for existing id`() {
        mockMvc.perform(get("/activities/${testActivity.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testActivity.id))
            .andExpect(jsonPath("$.name").value("Test Activity"))
            .andExpect(jsonPath("$.url").value("test-url"))
    }

    @Test
    fun `getActivityById returns 404 for non-existent id`() {
        mockMvc.perform(get("/activities/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteActivity successfully deletes existing activity`() {
        mockMvc.perform(delete("/activities/${testActivity.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)

        // Verify activity is deleted
        mockMvc.perform(get("/activities/${testActivity.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteActivity returns 404 for non-existent id`() {
        mockMvc.perform(delete("/activities/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }
}