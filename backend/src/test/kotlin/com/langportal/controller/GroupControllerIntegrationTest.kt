package com.langportal.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.langportal.model.Group
import com.langportal.model.Word
import com.langportal.repository.GroupRepository
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
class GroupControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var wordRepository: WordRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    private lateinit var testGroup: Group
    private lateinit var testWord: Word

    @BeforeEach
    fun setup() {
        // Clean up existing data
        groupRepository.deleteAll()
        wordRepository.deleteAll()
        entityManager.flush()

        // Create and save test word
        testWord = Word(
            sourceWord = "casa",
            targetWord = "house"
        ).let {
            wordRepository.save(it)
        }

        // Create and save test group
        testGroup = Group(
            name = "Test Group",
            description = "Test Description",
            wordsCount = 1,
            words = mutableListOf(testWord)
        ).let {
            groupRepository.save(it)
        }
        
        entityManager.flush()
    }

    @Test
    fun `getAllGroups returns list of groups`() {
        mockMvc.perform(get("/groups")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(testGroup.id))
            .andExpect(jsonPath("$[0].name").value("Test Group"))
            .andExpect(jsonPath("$[0].description").value("Test Description"))
    }

    @Test
    fun `getGroupById returns group for existing id`() {
        mockMvc.perform(get("/groups/${testGroup.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testGroup.id))
            .andExpect(jsonPath("$.name").value("Test Group"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.words[0].id").value(testWord.id))
    }

    @Test
    fun `getGroupById returns 404 for non-existent id`() {
        mockMvc.perform(get("/groups/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createGroup creates new group successfully`() {
        val newGroupName = "New Test Group"
        
        mockMvc.perform(post("/groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newGroupName))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(newGroupName))
    }

    @Test
    fun `updateGroup successfully updates existing group`() {
        val updatedGroup = Group(
            id = testGroup.id,
            name = "Updated Group",
            description = "Updated Description"
        )

        mockMvc.perform(put("/groups/${testGroup.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedGroup)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testGroup.id))
            .andExpect(jsonPath("$.name").value("Updated Group"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
    }

    @Test
    fun `updateGroup returns 404 for non-existent id`() {
        val nonExistentGroup = Group(
            id = 999L,
            name = "Non-existent Group",
            description = "Description"
        )

        mockMvc.perform(put("/groups/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nonExistentGroup)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteGroup successfully deletes existing group`() {
        mockMvc.perform(delete("/groups/${testGroup.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)

        // Verify group is deleted
        mockMvc.perform(get("/groups/${testGroup.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteGroup returns 404 for non-existent id`() {
        mockMvc.perform(delete("/groups/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getGroupStatistics returns statistics for existing group`() {
        mockMvc.perform(get("/groups/${testGroup.id}/stats")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalReviews").exists())
            .andExpect(jsonPath("$.correctReviews").exists())
            .andExpect(jsonPath("$.accuracy").exists())
    }

    @Test
    fun `getGroupStatistics returns 404 for non-existent group`() {
        mockMvc.perform(get("/groups/999/stats")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }
}