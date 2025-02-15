package com.langportal.controller

import com.langportal.dto.GroupDTO
import com.langportal.mapper.ModelMapper
import com.langportal.model.Group
import com.langportal.service.GroupService
import com.langportal.service.ReviewStatistics
import com.langportal.service.StatisticsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GroupControllerTest {
    private lateinit var groupService: GroupService
    private lateinit var statisticsService: StatisticsService
    private lateinit var modelMapper: ModelMapper
    private lateinit var groupController: GroupController

    @BeforeEach
    fun setup() {
        groupService = mockk(relaxed = true)
        statisticsService = mockk(relaxed = true)
        modelMapper = mockk(relaxed = true)
        groupController = GroupController(groupService, statisticsService, modelMapper)
    }

    @Test
    fun `getAllGroups returns list of groups`() {
        // given
        val group = Group(id = 1, name = "Test Group", description = "Test Description")
        val groupDTO = GroupDTO(id = 1, name = "Test Group", description = "Test Description", words = emptyList())
        every { groupService.getAllGroups() } returns listOf(group)
        every { modelMapper.toGroupWithoutWords(any()) } returns groupDTO

        // when
        val response = groupController.getAllGroups()

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body).hasSize(1)
        verify { groupService.getAllGroups() }
    }

    @Test
    fun `getGroupById returns group when found`() {
        // given
        val group = Group(id = 1, name = "Test Group", description = "Test Description")
        val groupDTO = GroupDTO(id = 1, name = "Test Group", description = "Test Description", words = emptyList())
        every { groupService.getGroup(1) } returns group
        every { modelMapper.toGroupDTO(group) } returns groupDTO

        // when
        val response = groupController.getGroupById(1)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.id).isEqualTo(1)
        verify { groupService.getGroup(1) }
    }

    @Test
    fun `createGroup returns created group`() {
        // given
        val groupName = "New Group"
        val createdGroup = Group(id = 1, name = groupName, description = "Test Description")
        val groupDTO = GroupDTO(id = 1, name = groupName, description = "Test Description", words = emptyList())
        every { groupService.createGroup(groupName) } returns createdGroup
        every { modelMapper.toGroupDTO(createdGroup) } returns groupDTO

        // when
        val response = groupController.createGroup(groupName)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.name).isEqualTo(groupName)
        verify { groupService.createGroup(groupName) }
    }

    @Test
    fun `updateGroup returns updated group`() {
        // given
        val groupId = 1L
        val group = Group(id = groupId, name = "Updated Group", description = "Updated Description")
        val groupDTO = GroupDTO(id = groupId, name = "Updated Group", description = "Updated Description", words = emptyList())
        every { groupService.updateGroup(groupId, group) } returns group
        every { modelMapper.toGroupDTO(group) } returns groupDTO

        // when
        val response = groupController.updateGroup(groupId, group)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.id).isEqualTo(groupId)
        verify { groupService.updateGroup(groupId, group) }
    }

    @Test
    fun `deleteGroup returns success`() {
        // given
        val groupId = 1L
        every { groupService.deleteGroup(groupId) } returns Unit

        // when
        val response = groupController.deleteGroup(groupId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        verify { groupService.deleteGroup(groupId) }
    }

    @Test
    fun `getGroupStatistics returns statistics`() {
        // given
        val groupId = 1L
        val stats = ReviewStatistics(totalReviews = 10, correctReviews = 8, accuracy = 0.8)
        every { statisticsService.getGroupStatistics(groupId) } returns stats

        // when
        val response = groupController.getGroupStatistics(groupId)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.totalReviews).isEqualTo(10)
        assertThat(response.body?.correctReviews).isEqualTo(8)
        assertThat(response.body?.accuracy).isEqualTo(0.8)
        verify { statisticsService.getGroupStatistics(groupId) }
    }
}
